package ru.demetrious.deus.bot.adapter.output.shikimori.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

@AllArgsConstructor
public class AnimesQuery implements Query {
    private int page;
    private int limit;

    @Override
    public String serialize() {
        return "animes(page:%s,limit:%s,status:\\\"released\\\"){airedOn{date},franchise,genres{kind,russian},japanese,name,origin,russian,score,studios{name},synonyms}".formatted(page, limit);
    }
}
