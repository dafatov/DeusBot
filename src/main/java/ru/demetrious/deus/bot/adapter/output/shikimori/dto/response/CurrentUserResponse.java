package ru.demetrious.deus.bot.adapter.output.shikimori.dto.response;

import lombok.Data;
import ru.demetrious.deus.bot.domain.graphql.ResponseSerialize;

@Data
public class CurrentUserResponse implements ResponseSerialize {
    private Integer id;
}
