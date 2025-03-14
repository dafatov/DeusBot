package ru.demetrious.deus.bot.adapter.output.shikimori.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.Query;

@AllArgsConstructor
public class CurrentUserQuery implements Query {
    @Override
    public String serialize() {
        return "currentUser{id}";
    }
}
