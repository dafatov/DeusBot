package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GraphQl mutation dto
 */
@AllArgsConstructor
public class SaveMediaListEntryAnilist implements MutationAnilist {
    @Getter
    private final String name = "SaveMediaListEntry";

    private MediaListStatusAnilist status;
    private Integer mediaId;
    private Integer progress;
    private Double score;
    private Integer repeat;

    @Override
    public String serialize() {
        return "SaveMediaListEntry(status:%s,mediaId:%d,progress:%d,score:%s,repeat:%d){id}".formatted(status, mediaId, progress, score, repeat);
    }
}
