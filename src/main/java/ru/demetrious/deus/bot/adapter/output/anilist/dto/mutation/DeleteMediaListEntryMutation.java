package ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Mutation;

/**
 * GraphQl mutation dto
 */
@AllArgsConstructor
public class DeleteMediaListEntryMutation implements Mutation {
    private Integer id;

    @Override
    public String serialize() {
        return "DeleteMediaListEntry(id:%d){deleted}".formatted(id);
    }
}
