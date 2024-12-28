package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GraphQl mutation dto
 */
@AllArgsConstructor
public class DeleteMediaListEntryAnilist implements MutationAnilist {
    @Getter
    private final String name = "DeleteMediaListEntry";

    private Integer id;

    @Override
    public String serialize() {
        return "DeleteMediaListEntry(id:%d){deleted}".formatted(id);
    }
}
