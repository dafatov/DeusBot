package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto.TeamDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto;

import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.AddHintActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.ChangeTeamActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.GetStateActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetHintGuessedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetLockedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetPauseActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.ShufflePlayersActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.StartGameActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.VoteActionDto;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

@JsonSubTypes({
    @Type(value = GetStateActionDto.class, name = CODE_NAMES + ".get_state"),
    @Type(value = ChangeTeamActionDto.class, name = CODE_NAMES + ".change_team"),
    @Type(value = StartGameActionDto.class, name = CODE_NAMES + ".start_game"),
    @Type(value = AddHintActionDto.class, name = CODE_NAMES + ".add_hint"),
    @Type(value = SetHintGuessedActionDto.class, name = CODE_NAMES + ".set_hint_guessed"),
    @Type(value = VoteActionDto.class, name = CODE_NAMES + ".vote"),
    @Type(value = ShufflePlayersActionDto.class, name = CODE_NAMES + ".shuffle_players"),
    @Type(value = SetLockedActionDto.class, name = CODE_NAMES + ".set_locked"),
    @Type(value = SetPauseActionDto.class, name = CODE_NAMES + ".set_pause"),
})
public interface CodeNamesActionDto extends ActionDto {
    @Builder
    record GetStateActionDto() implements CodeNamesActionDto {
    }

    @Builder
    record ChangeTeamActionDto(TeamDto team, boolean captain) implements CodeNamesActionDto {
    }

    @Builder
    record StartGameActionDto() implements CodeNamesActionDto {
    }

    @Builder
    record AddHintActionDto(String word, int count) implements CodeNamesActionDto {
    }

    @Builder
    record SetHintGuessedActionDto(String word, TeamDto team, int guessed) implements CodeNamesActionDto {
    }

    @Builder
    record VoteActionDto(VoteDto vote) implements CodeNamesActionDto {
    }

    @Builder
    record ShufflePlayersActionDto() implements CodeNamesActionDto {
    }

    @Builder
    record SetLockedActionDto() implements CodeNamesActionDto {
    }

    @Builder
    record SetPauseActionDto() implements CodeNamesActionDto {
    }
}
