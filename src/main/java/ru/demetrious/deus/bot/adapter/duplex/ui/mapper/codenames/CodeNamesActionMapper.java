package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.codenames;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.AddHintActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.ChangeTeamActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.GetStateActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetHintGuessedActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetLockedActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.SetPauseActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.ShufflePlayersActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.StartGameActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto.VoteActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.AddHintAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ChangeTeamAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetHintGuessedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.VoteAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto.SkipVoteDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto.WordVoteDto;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.WordVote;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface CodeNamesActionMapper {
    @SubclassMapping(target = GetStateAction.class, source = GetStateActionDto.class)
    @SubclassMapping(target = ChangeTeamAction.class, source = ChangeTeamActionDto.class)
    @SubclassMapping(target = StartGameAction.class, source = StartGameActionDto.class)
    @SubclassMapping(target = AddHintAction.class, source = AddHintActionDto.class)
    @SubclassMapping(target = SetHintGuessedAction.class, source = SetHintGuessedActionDto.class)
    @SubclassMapping(target = VoteAction.class, source = VoteActionDto.class)
    @SubclassMapping(target = ShufflePlayersAction.class, source = ShufflePlayersActionDto.class)
    @SubclassMapping(target = SetLockedAction.class, source = SetLockedActionDto.class)
    @SubclassMapping(target = SetPauseAction.class, source = SetPauseActionDto.class)
    CodeNamesAction map(CodeNamesActionDto actionDto);

    GetStateAction mapGetState(GetStateActionDto value);

    StartGameAction mapStartGame(StartGameActionDto value);

    ShufflePlayersAction mapShufflePlayers(ShufflePlayersActionDto value);

    SetLockedAction mapSetLocked(SetLockedActionDto value);

    SetPauseAction mapSetPause(SetPauseActionDto value);

    @SubclassMapping(target = WordVote.class, source = WordVoteDto.class)
    @SubclassMapping(target = SkipVote.class, source = SkipVoteDto.class)
    Vote map(VoteDto value);

    SkipVote mapSkip(SkipVoteDto value);

    // =========================================================================================================================================================

    @InheritInverseConfiguration
    CodeNamesActionDto map(CodeNamesAction action);

    GetStateActionDto mapGetState(GetStateAction value);

    StartGameActionDto mapStartGame(StartGameAction value);

    ShufflePlayersActionDto mapShufflePlayers(ShufflePlayersAction value);

    SetLockedActionDto mapSetLocked(SetLockedAction value);

    SetPauseActionDto mapSetPause(SetPauseAction value);

    @InheritInverseConfiguration
    VoteDto map(Vote vote);

    SkipVoteDto mapSkip(SkipVote skipVote);
}
