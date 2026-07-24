package ru.demetrious.deus.bot.adapter.output.anilist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.DeleteMediaListEntryMutation;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.SaveMediaListEntryMutation;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaListCollectionQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.PageQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists.Entries;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.PageMediaListResponse;
import ru.demetrious.deus.bot.adapter.output.anilist.mapper.AnimeAnilistMapper;
import ru.demetrious.deus.bot.app.api.anime.ImportAnimeOutbound;
import ru.demetrious.deus.bot.domain.Anime;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;
import ru.demetrious.deus.bot.domain.ImportAnimeContext.AnimeProjection;
import ru.demetrious.deus.bot.domain.graphql.Request;

import static java.net.URI.create;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.COMPLETED;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist.ANIME;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaListCollectionQuery.PER_CHUNK;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQueries;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQuery;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnilistAdapter implements ImportAnimeOutbound {
    private static final Supplier<String> RANDOM_KEY_SUPPLIER = () -> secure().nextAlphabetic(7);
    private static final int PER_PAGE = 50;
    private static final int PER_REQUEST = 150;

    private final AnilistClient anilistClient;
    private final AnimeAnilistMapper animeAnilistMapper;

    @Value("${SHIKIMORI_URL}")
    private String shikimoriUrl;

    @Override
    public ImportAnimeContext execute(List<Anime> targetAnimeList, Integer userId) {
        Map<Integer, Entries> oldEntriesByMalId = getOldAnimeMap(userId);
        List<Entries> toUpdate = new ArrayList<>();
        List<Entries> toReportAsAnother = new ArrayList<>();
        List<Integer> newMalIds = new ArrayList<>();

        for (Anime anime : targetAnimeList) {
            Entries newEntry = animeAnilistMapper.map(anime);
            Entries oldEntry = oldEntriesByMalId.remove(newEntry.getMedia().getIdMal());

            if (newEntry.equals(oldEntry)) {
                continue;
            }

            if (hasCompletedWithDifferentEpisodes(newEntry, oldEntry)) {
                toReportAsAnother.add(newEntry);
                continue;
            }

            if (isNull(oldEntry)) {
                newMalIds.add(newEntry.getMedia().getIdMal());
            } else {
                newEntry.getMedia().setId(oldEntry.getMedia().getId());
            }

            toUpdate.add(newEntry);
        }

        List<Entries> toDelete = new ArrayList<>(oldEntriesByMalId.values());
        List<Entries> toSkip = skipNewNotExisted(toUpdate, newMalIds);

        if (isNotEmpty(toUpdate)) {
            partition(toUpdate, PER_REQUEST).stream()
                .map(this::mapSaveMediaMutations)
                .map(Request::createMutations)
                .forEach(anilistClient::execute);
            log.info("Обновлены аниме для пользователя {}: {}", userId, toUpdate.size());
            log.trace("toUpdate: {}", toUpdate);
        }

        if (isNotEmpty(toDelete)) {
            partition(toDelete, PER_REQUEST).stream()
                .map(this::mapDeleteMediaMutations)
                .map(Request::createMutations)
                .forEach(anilistClient::execute);
            log.info("Удалены аниме для пользователя {}: {}", userId, toDelete.size());
            log.trace("toUpdate: {}", toDelete);
        }

        return new ImportAnimeContext()
            .setAdded(mapAnimeProjections(toUpdate.stream().filter(f -> newMalIds.contains(f.getMedia().getIdMal()))))
            .setEdited(mapAnimeProjections(toUpdate.stream().filter(f -> !newMalIds.contains(f.getMedia().getIdMal()))))
            .setRemoved(mapAnimeProjections(toDelete.stream()))
            .setSkipped(mapAnimeProjections(toSkip.stream()))
            .setAnother(mapAnimeProjections(toReportAsAnother.stream()));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private List<Entries> skipNewNotExisted(List<Entries> toUpdate, List<Integer> newMalIds) {
        List<Entries> toSkip = new ArrayList<>();
        Map<Integer, Integer> existingNewIdsMap = getExistingNewIdsMap(newMalIds);

        Iterator<Entries> iterator = toUpdate.iterator();
        while (iterator.hasNext()) {
            Entries entry = iterator.next();

            if (nonNull(entry.getMedia().getId())) {
                continue;
            }

            Integer id = existingNewIdsMap.get(entry.getMedia().getIdMal());

            if (isNull(id)) {
                toSkip.add(entry);
                iterator.remove();
            } else {
                entry.getMedia().setId(id);
            }
        }
        return toSkip;
    }

    private Map<String, DeleteMediaListEntryMutation> mapDeleteMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .map(entries -> new DeleteMediaListEntryMutation(entries.getId()))
            .collect(toMap(_ -> RANDOM_KEY_SUPPLIER.get(), identity()));
    }

    private Map<String, SaveMediaListEntryMutation> mapSaveMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .flatMap(this::mapSaveMediaMutation)
            .collect(toMap(_ -> RANDOM_KEY_SUPPLIER.get(), identity(), (a, _) -> a, LinkedHashMap::new));
    }

    private Stream<SaveMediaListEntryMutation> mapSaveMediaMutation(Entries entries) {
        SaveMediaListEntryMutation saveMediaListEntryAnilist = new SaveMediaListEntryMutation(
            entries.getStatus(),
            entries.getMedia().getId(),
            entries.getProgress(),
            entries.getScore(),
            entries.getRepeat()
        );

        if (COMPLETED == entries.getStatus() && entries.getProgress() < entries.getMedia().getEpisodes()) {
            log.debug("Added duplicate mutation for idMal={}", entries.getMedia().getIdMal());
            return of(saveMediaListEntryAnilist, new SaveMediaListEntryMutation(
                null,
                entries.getMedia().getId(),
                entries.getProgress(),
                null,
                null
            ));
        }

        return of(saveMediaListEntryAnilist);
    }

    private Map<Integer, Integer> getExistingNewIdsMap(List<Integer> malIdList) {
        Map<String, PageQuery> anilistMap = partition(malIdList, PER_PAGE).stream()
            .map(chunk -> new MediaQuery(ANIME, chunk))
            .map(query -> new PageQuery(query, 1, PER_PAGE))
            .collect(toMap(_ -> RANDOM_KEY_SUPPLIER.get(), identity()));

        return anilistClient.execute(createQueries(anilistMap))
            .getData().values().stream()
            .map(t -> getMapper().convertValue(t, PageMediaListResponse.class))
            .map(PageMediaListResponse::getMedia)
            .flatMap(Collection::stream)
            .collect(toMap(PageMediaListResponse.Media::getIdMal, PageMediaListResponse.Media::getId));
    }

    private Map<Integer, Entries> getOldAnimeMap(Integer userId) {
        String key = RANDOM_KEY_SUPPLIER.get();
        Map<Integer, Entries> oldAnimeMap = new HashMap<>();

        Map<Integer, Entries> entriesMapChunk;
        int chunk = 1;
        do {
            entriesMapChunk = anilistClient.execute(createQuery(key, new MediaListCollectionQuery(userId, chunk++)))
                .get(key, MediaListCollectionResponse.class)
                .getLists().stream()
                .map(Lists::getEntries)
                .flatMap(Collection::stream)
                .collect(toMap(oldAnime -> oldAnime.getMedia().getIdMal(), identity()));
            oldAnimeMap.putAll(entriesMapChunk);
        } while (entriesMapChunk.size() == PER_CHUNK);

        return oldAnimeMap;
    }

    private List<AnimeProjection> mapAnimeProjections(Stream<Entries> entriesStream) {
        return entriesStream.map(this::mapAnimeProjection).toList();
    }

    private AnimeProjection mapAnimeProjection(Entries entries) {
        return new AnimeProjection()
            .setTitle(entries.getMedia().getTitle().getRomaji())
            .setUrl(create("%s/animes/%d".formatted(shikimoriUrl, entries.getMedia().getIdMal())));
    }

    private static boolean hasCompletedWithDifferentEpisodes(@NotNull Entries anime, @Nullable Entries oldAnimeRemoved) {
        return !anime.equals(oldAnimeRemoved) && nonNull(oldAnimeRemoved)
            && anime.getStatus() == COMPLETED
            && oldAnimeRemoved.getStatus() == COMPLETED
            && !Objects.equals(anime.getMedia().getEpisodes(), oldAnimeRemoved.getMedia().getEpisodes());
    }
}
