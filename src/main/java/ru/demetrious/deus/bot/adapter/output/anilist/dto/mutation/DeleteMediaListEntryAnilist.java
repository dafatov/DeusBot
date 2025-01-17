package ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.MutationAnilist;

/**
 * GraphQl mutation dto
 */
@AllArgsConstructor
public class DeleteMediaListEntryAnilist implements MutationAnilist {
    private Integer id;

    @Override
    public String serialize() {
        return "DeleteMediaListEntry(id:%d){deleted}".formatted(id);
    }
}
