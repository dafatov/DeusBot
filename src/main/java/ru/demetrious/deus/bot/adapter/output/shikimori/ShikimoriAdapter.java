package ru.demetrious.deus.bot.adapter.output.shikimori;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.query.AnimesQuery;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.query.CurrentUserQuery;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.query.UserRatesQuery;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.AnimeResponse;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.CurrentUserResponse;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.response.UserRateResponse;
import ru.demetrious.deus.bot.adapter.output.shikimori.mapper.AnimeShikimoriMapper;
import ru.demetrious.deus.bot.adapter.output.shikimori.mapper.FranchiseShikimoriMapper;
import ru.demetrious.deus.bot.app.api.anime.GetAnimeOutbound;
import ru.demetrious.deus.bot.app.api.anime.GetFranchiseOutbound;
import ru.demetrious.deus.bot.domain.Anime;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.graphql.Request;
import ru.demetrious.deus.bot.domain.graphql.Response;

import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static ru.demetrious.deus.bot.adapter.output.shikimori.dto.query.UserRatesQuery.TargetType.ANIME;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQueries;
import static ru.demetrious.deus.bot.domain.graphql.Request.createQuery;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getXmlMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShikimoriAdapter implements GetAnimeOutbound, GetFranchiseOutbound {
    private static final Supplier<String> RANDOM_KEY_SUPPLIER = () -> randomAlphabetic(7);
    private static final int PER_PAGE = 50;
    private static final int PER_CHUNK = 2;

    private final ShikimoriClient shikimoriClient;
    private final AnimeShikimoriMapper animeShikimoriMapper;
    private final FranchiseShikimoriMapper franchiseShikimoriMapper;

    @Override
    public List<Anime> getAnimeList() {
        return animeShikimoriMapper.map(getUserRateResponseList());
    }

    @Override
    public byte[] getAnimeListXml() throws JsonProcessingException {
        return getXmlMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsBytes(animeShikimoriMapper.mapXml(getUserRateResponseList()));
    }

    @Cacheable(value = "shikimori-franchises", sync = true)
    @Override
    public List<Franchise> getFranchiseList() {
        List<AnimeResponse> animeResponseList = new ArrayList<>();

        List<AnimeResponse> animeResponseChunk;
        int page = 1;
        do {
            Map<String, AnimesQuery> franchisesQueries = range(page, page += PER_CHUNK)
                .mapToObj(i -> new AnimesQuery(i, PER_PAGE))
                .collect(toMap(q -> RANDOM_KEY_SUPPLIER.get(), identity()));
            Response response = shikimoriClient.execute(createQueries(franchisesQueries));

            animeResponseChunk = response.getPowerList(AnimeResponse.class).stream()
                .flatMap(Collection::stream)
                .toList();
            animeResponseList.addAll(animeResponseChunk);
        } while (animeResponseChunk.size() >= PER_PAGE * PER_CHUNK);

        return franchiseShikimoriMapper.map(animeResponseList.stream()
            .filter(a -> nonNull(a.getFranchise()))
            .collect(groupingBy(AnimeResponse::getFranchise)));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private List<UserRateResponse> getUserRateResponseList() {
        String key = RANDOM_KEY_SUPPLIER.get();
        Request query = createQuery(key, new CurrentUserQuery());
        Integer userId = shikimoriClient.execute(query).get(key, CurrentUserResponse.class).getId();
        List<UserRateResponse> userRateResponseList = new ArrayList<>();

        List<UserRateResponse> userRateResponseChunk;
        int page = 1;
        do {
            Map<String, UserRatesQuery> userRatesQueries = range(page, page += PER_CHUNK)
                .mapToObj(i -> new UserRatesQuery(userId, i, PER_PAGE, ANIME))
                .collect(toMap(q -> RANDOM_KEY_SUPPLIER.get(), identity()));
            Response response = shikimoriClient.execute(createQueries(userRatesQueries));

            userRateResponseChunk = response.getPowerList(UserRateResponse.class).stream()
                .flatMap(Collection::stream)
                .toList();
            userRateResponseList.addAll(userRateResponseChunk);
        } while (userRateResponseChunk.size() >= PER_PAGE * PER_CHUNK);

        return userRateResponseList;
    }
}
