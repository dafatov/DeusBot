package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import lombok.AllArgsConstructor;

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
