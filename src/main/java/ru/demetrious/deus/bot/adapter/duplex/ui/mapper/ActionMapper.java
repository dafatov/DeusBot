package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.AddHintActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.ChangeTeamActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.GetStateActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetHintGuessedActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetLockedActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.SetPauseActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.ShufflePlayersActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.StartGameActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto.VoteActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.AddHintAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ChangeTeamAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetHintGuessedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.VoteAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.SkipVoteDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.WordVoteDto;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.WordVote;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface ActionMapper {
    @SubclassMapping(target = GetStateAction.class, source = GetStateActionDto.class)
    @SubclassMapping(target = ChangeTeamAction.class, source = ChangeTeamActionDto.class)
    @SubclassMapping(target = StartGameAction.class, source = StartGameActionDto.class)
    @SubclassMapping(target = AddHintAction.class, source = AddHintActionDto.class)
    @SubclassMapping(target = SetHintGuessedAction.class, source = SetHintGuessedActionDto.class)
    @SubclassMapping(target = VoteAction.class, source = VoteActionDto.class)
    @SubclassMapping(target = ShufflePlayersAction.class, source = ShufflePlayersActionDto.class)
    @SubclassMapping(target = SetLockedAction.class, source = SetLockedActionDto.class)
    @SubclassMapping(target = SetPauseAction.class, source = SetPauseActionDto.class)
    Action map(ActionDto actionDto);

    GetStateAction map(GetStateActionDto value);

    StartGameAction map(StartGameActionDto value);

    ShufflePlayersAction map(ShufflePlayersActionDto value);

    SetLockedAction map(SetLockedActionDto value);

    SetPauseAction map(SetPauseActionDto value);

    @SubclassMapping(target = WordVote.class, source = WordVoteDto.class)
    @SubclassMapping(target = SkipVote.class, source = SkipVoteDto.class)
    Vote map(VoteDto value);

    SkipVote map(SkipVoteDto value);
}
