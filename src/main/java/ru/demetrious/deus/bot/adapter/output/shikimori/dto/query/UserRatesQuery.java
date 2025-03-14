package ru.demetrious.deus.bot.adapter.output.shikimori.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

@AllArgsConstructor
public class UserRatesQuery implements Query {
    private int userId;
    private int page;
    private int limit;
    private TargetType targetType;

    @Override
    public String serialize() {
        return "userRates(userId:%s,page:%s,limit:%s,targetType:%s){anime{name,kind,episodes,malId},episodes,rewatches,score,status,text,createdAt,updatedAt}"
            .formatted(userId, page, limit, targetType.getValue());
    }

    @Getter
    @RequiredArgsConstructor
    public enum TargetType {
        ANIME("Anime"),
        MANGA("Manga");

        private final String value;
    }
}
