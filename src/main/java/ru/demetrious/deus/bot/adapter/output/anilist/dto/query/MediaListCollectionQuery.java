package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist;
import ru.demetrious.deus.bot.domain.graphql.Query;

/**
 * GraphQl query dto
 */
@AllArgsConstructor
public class MediaListCollectionQuery implements Query {
    private Integer userId;
    private MediaTypeAnilist type;

    @Override
    public String serialize() {
        return "MediaListCollection(userId:%d,type:%s){lists{entries{id,media{idMal},progress,repeat,score,status}}}".formatted(userId, type);
    }
}
