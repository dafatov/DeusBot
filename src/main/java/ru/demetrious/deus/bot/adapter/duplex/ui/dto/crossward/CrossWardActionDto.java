package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;

import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.GetStateActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetLockedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetPauseActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetSpectatorActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.ShufflePlayersActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SkipTurnActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.StartGameActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SubmitWordActionDto;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateActionDto.class, name = CROSS_WARD + ".get_state"),
    @Type(value = StartGameActionDto.class, name = CROSS_WARD + ".start_game"),
    @Type(value = SetSpectatorActionDto.class, name = CROSS_WARD + ".set_spectator"),
    @Type(value = SetLockedActionDto.class, name = CROSS_WARD + ".set_locked"),
    @Type(value = SetPauseActionDto.class, name = CROSS_WARD + ".set_pause"),
    @Type(value = ShufflePlayersActionDto.class, name = CROSS_WARD + ".shuffle_players"),
    @Type(value = SubmitWordActionDto.class, name = CROSS_WARD + ".submit_word"),
    @Type(value = SkipTurnActionDto.class, name = CROSS_WARD + ".skip_turn"),
})
public interface CrossWardActionDto extends ActionDto {
    @Builder
    record GetStateActionDto() implements CrossWardActionDto {
    }

    @Builder
    record StartGameActionDto() implements CrossWardActionDto {
    }

    @Builder
    record SetSpectatorActionDto(boolean spectator) implements CrossWardActionDto {
    }

    @Builder
    record SetLockedActionDto() implements CrossWardActionDto {
    }

    @Builder
    record SetPauseActionDto() implements CrossWardActionDto {
    }

    @Builder
    record ShufflePlayersActionDto() implements CrossWardActionDto {
    }

    @Builder
    record SubmitWordActionDto(int wordId, String word) implements CrossWardActionDto {
    }

    @Builder
    record SkipTurnActionDto() implements CrossWardActionDto {
    }
}
