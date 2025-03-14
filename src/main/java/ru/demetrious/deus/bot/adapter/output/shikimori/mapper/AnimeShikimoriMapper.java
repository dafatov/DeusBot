package ru.demetrious.deus.bot.adapter.output.shikimori.mapper;

import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.UserRateResponse;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.xml.AnimeListDto;
import ru.demetrious.deus.bot.domain.Anime;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.UserRateResponse.Status.COMPLETED;

@Mapper
public interface AnimeShikimoriMapper {
    default AnimeListDto mapXml(List<UserRateResponse> animes) {
        return new AnimeListDto().setAnimeList(mapXmlList(animes));
    }

    List<AnimeListDto.Anime> mapXmlList(List<UserRateResponse> anime);

    @Mapping(target = "watchedEpisodes", source = "episodes")
    @Mapping(target = "episodes", source = "anime.episodes")
    @Mapping(target = "shikiStatus", source = "status", qualifiedByName = "mapShikiStatusXml")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusXml")
    @Mapping(target = "name", source = "anime.name")
    @Mapping(target = "malId", source = "anime.malId")
    @Mapping(target = "kind", source = "anime.kind")
    AnimeListDto.Anime mapXml(UserRateResponse anime);

    List<Anime> map(List<UserRateResponse> animes);

    @Mapping(target = "episodes", source = "anime.episodes")
    @Mapping(target = "watchedEpisodes", source = "episodes")
    @Mapping(target = "type", source = "anime.kind")
    @Mapping(target = "title", source = "anime.name")
    @Mapping(target = "rewatched", source = "rewatches")
    @Mapping(target = "comment", source = "text")
    @Mapping(target = "animedbId", source = "anime.malId")
    Anime map(UserRateResponse anime);

    @ValueMapping(target = "REPEATING", source = "REWATCHING")
    @ValueMapping(target = "PAUSED", source = "ON_HOLD")
    Anime.Status map(UserRateResponse.Status anime);

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    @Named("mapStatusXml")
    default String mapStatusXml(UserRateResponse.Status status) {
        return switch (status) {
            case PLANNED, WATCHING, COMPLETED, ON_HOLD, DROPPED -> mapShikiStatusXml(status);
            case REWATCHING -> mapShikiStatusXml(COMPLETED);
        };
    }

    @Named("mapShikiStatusXml")
    default String mapShikiStatusXml(UserRateResponse.Status status) {
        return switch (status) {
            case PLANNED -> "Plan to Watch";
            case WATCHING -> "Watching";
            case REWATCHING -> "Rewatching";
            case COMPLETED -> "Completed";
            case ON_HOLD -> "On-Hold";
            case DROPPED -> "Dropped";
        };
    }

    default String mapDateXml(Instant instant) {
        return ISO_LOCAL_DATE.withZone(systemDefault()).format(instant);
    }
}
