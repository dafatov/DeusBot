package ru.demetrious.deus.bot.adapter.output.shikimori.dto.query;

import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

@RequiredArgsConstructor
public class AnimesQuery implements Query {
    private final int page;
    private final int limit;

    @Override
    public String serialize() {
        return ("animes(page:%s,limit:%s,status:%s){airedOn{date},duration,episodes,episodesAired,franchise,genres{kind,russian},japanese,kind,name,origin,russian,score,studios{name},synonyms,url}")
            .formatted(page, limit, "\\\"!anons\\\"");
    }
}
