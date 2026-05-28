package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto.TeamDto;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.AddHintActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.ChangeTeamActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.GetStateActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetHintGuessedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetLockedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetPauseActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.ShufflePlayersActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.StartGameActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.VoteActionDto;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = GetStateActionDto.class, name = "get_state"),
    @Type(value = ChangeTeamActionDto.class, name = "change_team"),
    @Type(value = StartGameActionDto.class, name = "start_game"),
    @Type(value = AddHintActionDto.class, name = "add_hint"),
    @Type(value = SetHintGuessedActionDto.class, name = "set_hint_guessed"),
    @Type(value = VoteActionDto.class, name = "vote"),
    @Type(value = ShufflePlayersActionDto.class, name = "shuffle_players"),
    @Type(value = SetLockedActionDto.class, name = "set_locked"),
    @Type(value = SetPauseActionDto.class, name = "set_pause"),
})
public interface ActionDto {
    @Builder
    record GetStateActionDto() implements ActionDto {
    }

    @Builder
    record ChangeTeamActionDto(TeamDto team, boolean captain) implements ActionDto {
    }

    @Builder
    record StartGameActionDto() implements ActionDto {
    }

    @Builder
    record AddHintActionDto(String word, int count) implements ActionDto {
    }

    @Builder
    record SetHintGuessedActionDto(String word, TeamDto team, int guessed) implements ActionDto {
    }

    @Builder
    record VoteActionDto(VoteDto vote) implements ActionDto {
    }

    @Builder
    record ShufflePlayersActionDto() implements ActionDto {
    }

    @Builder
    record SetLockedActionDto() implements ActionDto {
    }

    @Builder
    record SetPauseActionDto() implements ActionDto {
    }
}
