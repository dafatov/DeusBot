package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;

import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.GetStateActionDto;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateActionDto.class, name = CROSS_WARD + ".get_state"),
})
public interface CrossWardActionDto extends ActionDto {
    @Builder
    record GetStateActionDto() implements CrossWardActionDto {
    }
}
