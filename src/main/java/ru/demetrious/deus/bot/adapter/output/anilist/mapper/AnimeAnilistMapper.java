package ru.demetrious.deus.bot.adapter.output.anilist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists.Entries;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.response.MediaListCollectionResponse.Lists.Entries.Media;
import ru.demetrious.deus.bot.domain.Anime;
import ru.demetrious.deus.bot.domain.Anime.Status;

@Mapper
public interface AnimeAnilistMapper {
    @Mapping(target = "media", source = "anime")
    @Mapping(target = "repeat", source = "rewatched")
    @Mapping(target = "progress", source = "watchedEpisodes")
    @Mapping(target = "id", ignore = true)
    Entries map(Anime anime);

    @Mapping(target = "idMal", source = "animedbId")
    @Mapping(target = "id", ignore = true)
    Media mapMedia(Anime anime);

    @ValueMapping(source = "WATCHING", target = "CURRENT")
    @ValueMapping(source = "PLANNED", target = "PLANNING")
    MediaListStatusAnilist mapStatus(Status status);
}
