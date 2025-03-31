package ru.demetrious.deus.bot.adapter.output.shikimori.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Genre;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.IncompleteDate;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Origin;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Studio;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.Franchise.Source;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Genre.Kind.GENRE;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Genre.Kind.THEME;

@Mapper
public interface FranchiseShikimoriMapper {
    default List<Franchise> map(Map<String, List<AnimeResponse>> franchises) {
        return franchises.entrySet().stream()
            .map(franchise -> new Franchise()
                .setName(franchise.getKey())
                .setFirstTitle(franchise.getValue().stream()
                    .min(comparing(a -> a.getAiredOn().getDate()))
                    .map(animeResponse -> defaultIfBlank(animeResponse.getRussian(), animeResponse.getName()))
                    .orElseThrow())
                .setAverageScore(franchise.getValue().stream()
                    .map(AnimeResponse::getScore)
                    .flatMapToDouble(DoubleStream::of)
                    .average()
                    .orElseThrow())
                .setGenres(franchise.getValue().stream()
                    .map(AnimeResponse::getGenres)
                    .flatMap(Collection::stream)
                    .filter(g -> g.getKind() == GENRE)
                    .map(Genre::getRussian)
                    .collect(toSet()))
                .setThemes(franchise.getValue().stream()
                    .map(AnimeResponse::getGenres)
                    .flatMap(Collection::stream)
                    .filter(g -> g.getKind() == THEME)
                    .map(Genre::getRussian)
                    .collect(toSet()))
                .setTitles(franchise.getValue().stream()
                    .mapMulti(this::mapTitles)
                    .filter(StringUtils::isNoneBlank)
                    .collect(toSet()))
                .setMinAiredOnYear(franchise.getValue().stream()
                    .map(AnimeResponse::getAiredOn)
                    .min(comparing(IncompleteDate::getDate))
                    .map(IncompleteDate::getYear)
                    .orElseThrow())
                .setSources(franchise.getValue().stream()
                    .map(AnimeResponse::getOrigin)
                    .map(this::map)
                    .collect(toSet()))
                .setStudios(franchise.getValue().stream()
                    .map(AnimeResponse::getStudios)
                    .flatMap(Collection::stream)
                    .map(Studio::getName)
                    .collect(toSet())))
            .toList();
    }

    Source map(Origin origin);

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void mapTitles(AnimeResponse animeResponse, Consumer<String> objectConsumer) {
        objectConsumer.accept(animeResponse.getName());
        objectConsumer.accept(animeResponse.getRussian());
        objectConsumer.accept(animeResponse.getJapanese());
        animeResponse.getSynonyms().forEach(objectConsumer);
    }
}
