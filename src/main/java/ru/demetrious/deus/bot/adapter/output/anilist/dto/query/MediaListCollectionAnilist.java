package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist;

/**
 * GraphQl query dto
 */
@AllArgsConstructor
public class MediaListCollectionAnilist implements QueryAnilist {
    private Integer userId;
    private MediaTypeAnilist type;

    @Override
    public String serialize() {
        return "MediaListCollection(userId:%d,type:%s){lists{entries{id,media{idMal},progress,repeat,score,status}}}".formatted(userId, type);
    }
}
