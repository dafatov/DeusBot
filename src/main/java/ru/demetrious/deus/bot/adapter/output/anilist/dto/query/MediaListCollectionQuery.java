package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

import static ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist.ANIME;

/**
 * GraphQl query dto
 */
@RequiredArgsConstructor
public class MediaListCollectionQuery implements Query {
    private final Integer userId;
    private final Integer chunk;

    @Override
    public String serialize() {
        return "MediaListCollection(userId:%d,type:%s,chunk:%s,perChunk:%s){lists{entries{id,media{idMal},progress,repeat,score,status}}}"
            .formatted(userId, ANIME, chunk, 500);
    }
}
