package ru.demetrious.deus.bot.adapter.output.anilist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.DeleteMediaListEntryMutation;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.SaveMediaListEntryMutation;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaListCollectionQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.PageQuery;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists.Entries;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists.Entries.Media;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.PageMediaListResponse;
import ru.demetrious.deus.bot.adapter.output.anilist.mapper.AnimeAnilistMapper;
import ru.demetrious.deus.bot.app.api.anime.ImportAnimeOutbound;
import ru.demetrious.deus.bot.domain.Anime;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;
import ru.demetrious.deus.bot.domain.graphql.Request;

import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.COMPLETED;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist.ANIME;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQueries;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQuery;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnilistAdapter implements ImportAnimeOutbound {
    private static final Supplier<String> RANDOM_KEY_SUPPLIER = () -> randomAlphabetic(7);
    private static final int PER_PAGE = 50;
    private static final int PER_REQUEST = 150;

    private final AnilistClient anilistClient;
    private final AnimeAnilistMapper animeAnilistMapper;

    @Override
    public ImportAnimeContext execute(List<Anime> targetAnimeList, Integer userId) {
        Map<Integer, Entries> oldAnimeMap = getOldAnimeMap(userId);
        List<Entries> changedAndNewAnimes = new ArrayList<>();

        targetAnimeList.stream().map(animeAnilistMapper::map).forEach(anime -> {
            if (!anime.equals(oldAnimeMap.remove(anime.getMedia().getIdMal()))) {
                changedAndNewAnimes.add(anime);
            }
        });

        Map<Integer, Integer> existingNewIdsMap = getExistingNewIdsMap(changedAndNewAnimes);

        changedAndNewAnimes.removeIf(entries -> {
            Integer id = existingNewIdsMap.get(entries.getMedia().getIdMal());

            entries.getMedia().setId(id);
            return isNull(id);
        });

        if (isNotEmpty(changedAndNewAnimes)) {
            partition(changedAndNewAnimes, PER_REQUEST).stream()
                .map(this::mapSaveMediaMutations)
                .map(Request::createMutations)
                .forEach(anilistClient::execute);
            log.info("Обновлены аниме для пользователя {}: {}", userId, changedAndNewAnimes);
        }

        if (isNotEmpty(oldAnimeMap)) {
            List<Entries> oldAnimeList = new ArrayList<>(oldAnimeMap.values());

            partition(oldAnimeList, PER_REQUEST).stream()
                .map(this::mapDeleteMediaMutations)
                .map(Request::createMutations)
                .forEach(anilistClient::execute);
            log.info("Удалены аниме для пользователя {}: {}", userId, oldAnimeList);
        }

        return new ImportAnimeContext()
            .setChangesCount(changedAndNewAnimes.size())
            .setRemovedCount(oldAnimeMap.size());
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Map<String, DeleteMediaListEntryMutation> mapDeleteMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .map(entries -> new DeleteMediaListEntryMutation(entries.getId()))
            .collect(toMap(j -> RANDOM_KEY_SUPPLIER.get(), identity()));
    }

    private Map<String, SaveMediaListEntryMutation> mapSaveMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .flatMap(this::mapSaveMediaMutation)
            .collect(toMap(h -> RANDOM_KEY_SUPPLIER.get(), identity(), (a, b) -> a, LinkedHashMap::new));
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
            return Stream.of(saveMediaListEntryAnilist, new SaveMediaListEntryMutation(
                null,
                entries.getMedia().getId(),
                entries.getProgress(),
                null,
                null
            ));
        }

        return Stream.of(saveMediaListEntryAnilist);
    }

    private Map<Integer, Integer> getExistingNewIdsMap(List<Entries> changedAndNewAnimes) {
        Map<String, PageQuery> anilistMap = partition(changedAndNewAnimes, PER_PAGE).stream()
            .map(entriesList -> entriesList.stream().map(Entries::getMedia).map(Media::getIdMal).toList())
            .map(mediaIdList -> new MediaQuery(ANIME, mediaIdList))
            .map(mediaAnilist -> new PageQuery(mediaAnilist, 1, PER_PAGE))
            .collect(toMap(m -> RANDOM_KEY_SUPPLIER.get(), identity()));

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
        } while (!entriesMapChunk.isEmpty());

        return oldAnimeMap;
    }
}
