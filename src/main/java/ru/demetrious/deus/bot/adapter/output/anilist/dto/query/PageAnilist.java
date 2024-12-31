package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist;

@AllArgsConstructor
public class PageAnilist implements QueryAnilist {
    private QueryAnilist query;
    private Integer page;
    private Integer perPage;

    @Override
    public String serialize() {
        return "Page(page:%d,perPage:%d){%s,pageInfo{hasNextPage}}".formatted(page, perPage, query.serialize());
    }
}
