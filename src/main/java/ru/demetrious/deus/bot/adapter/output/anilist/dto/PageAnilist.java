package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import lombok.AllArgsConstructor;

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
