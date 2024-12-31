package ru.demetrious.deus.bot.adapter.output.anilist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.DeleteMediaListEntryAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation.SaveMediaListEntryAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.MediaListCollectionAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.query.PageAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionRsAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionRsAnilist.Lists;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionRsAnilist.Lists.Entries;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionRsAnilist.Lists.Entries.Media;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.PageMediaListRsAnilist;
import ru.demetrious.deus.bot.app.api.anime.ImportAnimeOutbound;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestAnilist.createQueries;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestAnilist.createQuery;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.COMPLETED;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.CURRENT;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.DROPPED;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.PAUSED;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.PLANNING;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist.REPEATING;
import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist.ANIME;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnilistAdapter implements ImportAnimeOutbound {
    private static final Supplier<String> RANDOM_KEY_SUPPLIER = () -> randomAlphabetic(7);
    private static final int PER_PAGE = 50;
    private static final int PER_REQUEST = 150;

    private final AnilistClient anilistClient;

    @Override
    public ImportAnimeContext execute(List<Map<String, String>> targetAnimeList, Integer userId) {
        Map<Integer, Entries> oldAnimeMap = getOldAnimeList(userId);
        List<Entries> changedAndNewAnimes = new ArrayList<>();

        targetAnimeList.stream().map(this::mapTargetAnime).forEach(anime -> {
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
                .map(RequestAnilist::createMutations)
                .forEach(anilistClient::execute);
            log.info("Обновлены аниме для пользователя {}: {}", userId, changedAndNewAnimes);
        }

        if (isNotEmpty(oldAnimeMap)) {
            List<Entries> oldAnimeList = new ArrayList<>(oldAnimeMap.values());

            partition(oldAnimeList, PER_REQUEST).stream()
                .map(this::mapDeleteMediaMutations)
                .map(RequestAnilist::createMutations)
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

    private Map<String, DeleteMediaListEntryAnilist> mapDeleteMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .map(entries -> new DeleteMediaListEntryAnilist(entries.getId()))
            .collect(toMap(j -> RANDOM_KEY_SUPPLIER.get(), identity()));
    }

    private Map<String, SaveMediaListEntryAnilist> mapSaveMediaMutations(List<Entries> entriesList) {
        return entriesList.stream()
            .flatMap(this::mapSaveMediaMutation)
            .collect(toMap(h -> RANDOM_KEY_SUPPLIER.get(), identity(), (a, b) -> a, LinkedHashMap::new));
    }

    private Stream<SaveMediaListEntryAnilist> mapSaveMediaMutation(Entries entries) {
        SaveMediaListEntryAnilist saveMediaListEntryAnilist = new SaveMediaListEntryAnilist(
            entries.getStatus(),
            entries.getMedia().getId(),
            entries.getProgress(),
            entries.getScore(),
            entries.getRepeat()
        );

        if (COMPLETED == entries.getStatus() && entries.getProgress() < entries.getMedia().getEpisodes()) {
            log.debug("Added duplicate mutation for idMal={}", entries.getMedia().getIdMal());
            return Stream.of(saveMediaListEntryAnilist, new SaveMediaListEntryAnilist(
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
        Map<String, PageAnilist> anilistMap = partition(changedAndNewAnimes, PER_PAGE).stream()
            .map(entriesList -> entriesList.stream().map(Entries::getMedia).map(Media::getIdMal).toList())
            .map(mediaIdList -> new MediaAnilist(ANIME, mediaIdList))
            .map(mediaAnilist -> new PageAnilist(mediaAnilist, 1, PER_PAGE))
            .collect(toMap(m -> RANDOM_KEY_SUPPLIER.get(), identity()));

        return anilistClient.execute(createQueries(anilistMap))
            .getData().values().stream()
            .map(t -> getMapper().convertValue(t, PageMediaListRsAnilist.class))
            .map(PageMediaListRsAnilist::getMedia)
            .flatMap(Collection::stream)
            .collect(toMap(PageMediaListRsAnilist.Media::getIdMal, PageMediaListRsAnilist.Media::getId));
    }

    private Map<Integer, Entries> getOldAnimeList(Integer userId) {
        String key = RANDOM_KEY_SUPPLIER.get();

        return anilistClient.execute(createQuery(key, new MediaListCollectionAnilist(userId, ANIME)))
            .get(key, MediaListCollectionRsAnilist.class)
            .getLists().stream()
            .map(Lists::getEntries)
            .flatMap(Collection::stream)
            .collect(toMap(oldAnime -> oldAnime.getMedia().getIdMal(), identity()));
    }

    private Entries mapTargetAnime(Map<String, String> animeProperties) {
        return new Entries(
            null,
            new Media(null, parseInt(animeProperties.get("series_animedb_id")), parseInt(animeProperties.get("series_episodes"))),
            parseInt(animeProperties.get("my_watched_episodes")),
            parseInt(animeProperties.get("my_times_watched")),
            parseDouble(animeProperties.get("my_score")),
            switch (animeProperties.get("shiki_status")) {
                case "Completed" -> COMPLETED;
                case "Dropped" -> DROPPED;
                case "On-Hold" -> PAUSED;
                case "Plan to Watch" -> PLANNING;
                case "Watching" -> CURRENT;
                case "Rewatching" -> REPEATING;
                default -> throw new IllegalStateException("Unexpected value: " + animeProperties.get("shiki_status"));
            }
        );
    }
}
