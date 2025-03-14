package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

@AllArgsConstructor
public class PageQuery implements Query {
    private Query query;
    private Integer page;
    private Integer perPage;

    @Override
    public String serialize() {
        return "Page(page:%d,perPage:%d){%s,pageInfo{hasNextPage}}".formatted(page, perPage, query.serialize());
    }
}
