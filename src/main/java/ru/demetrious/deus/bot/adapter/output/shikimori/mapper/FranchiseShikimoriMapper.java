package ru.demetrious.deus.bot.adapter.output.shikimori.mapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import static java.lang.Math.divideExact;
import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Genre.Kind.GENRE;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Genre.Kind.THEME;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Kind.CM;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Kind.MUSIC;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse.Kind.PV;

@Mapper
public interface FranchiseShikimoriMapper {
    default List<Franchise> map(Map<String, List<AnimeResponse>> franchises) {
        return franchises.entrySet().stream()
            .map(franchise -> {
                List<AnimeResponse> animeResponseList = franchise.getValue().stream()
                    .filter(animeResponse -> nonNull(animeResponse.getAiredOn().getDate()) && animeResponse.getScore() > 0
                        && !List.of(MUSIC, CM, PV).contains(animeResponse.getKind()))
                    .toList();

                if (animeResponseList.isEmpty()) {
                    return null;
                }

                int episodes = animeResponseList.stream()
                    .mapToInt(a -> a.getEpisodes() == 0 ? a.getEpisodesAired() : a.getEpisodes())
                    .sum();

                return new Franchise()
                    .setName(franchise.getKey())
                    .setFirstTitle(animeResponseList.stream()
                        .min(comparing(a -> a.getAiredOn().getDate()))
                        .map(animeResponse -> defaultIfBlank(animeResponse.getRussian(), animeResponse.getName()))
                        .orElseThrow())
                    .setAverageScore(animeResponseList.stream()
                        .map(AnimeResponse::getScore)
                        .flatMapToDouble(DoubleStream::of)
                        .average()
                        .orElseThrow())
                    .setGenres(animeResponseList.stream()
                        .map(AnimeResponse::getGenres)
                        .flatMap(Collection::stream)
                        .filter(g -> g.getKind() == GENRE)
                        .map(Genre::getRussian)
                        .collect(toSet()))
                    .setThemes(animeResponseList.stream()
                        .map(AnimeResponse::getGenres)
                        .flatMap(Collection::stream)
                        .filter(g -> g.getKind() == THEME)
                        .map(Genre::getRussian)
                        .collect(toSet()))
                    .setTitles(animeResponseList.stream()
                        .mapMulti(this::mapTitles)
                        .filter(StringUtils::isNoneBlank)
                        .collect(toSet()))
                    .setMinAiredOnYear(animeResponseList.stream()
                        .map(AnimeResponse::getAiredOn)
                        .min(comparing(IncompleteDate::getDate))
                        .map(IncompleteDate::getDate)
                        .map(LocalDate::getYear)
                        .orElseThrow())
                    .setSources(animeResponseList.stream()
                        .map(AnimeResponse::getOrigin)
                        .map(this::map)
                        .collect(toSet()))
                    .setStudios(animeResponseList.stream()
                        .map(AnimeResponse::getStudios)
                        .flatMap(Collection::stream)
                        .map(Studio::getName)
                        .collect(toSet()))
                    .setEpisodes(episodes)
                    .setAverageDuration(divideExact(60 * animeResponseList.stream()
                        .mapToInt(a -> a.getDuration() * a.getEpisodes())
                        .sum(), episodes));
            })
            .filter(Objects::nonNull)
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
