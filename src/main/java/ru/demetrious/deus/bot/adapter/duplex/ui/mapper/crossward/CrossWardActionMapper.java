package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.GetStateActionDto;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetSpectatorAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SkipTurnAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SubmitWordAction;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetLockedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetPauseActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetSpectatorActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.ShufflePlayersActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SkipTurnActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.StartGameActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SubmitWordActionDto;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface CrossWardActionMapper {
    @SubclassMapping(target = GetStateAction.class, source = GetStateActionDto.class)
    @SubclassMapping(target = StartGameAction.class, source = StartGameActionDto.class)
    @SubclassMapping(target = SetSpectatorAction.class, source = SetSpectatorActionDto.class)
    @SubclassMapping(target = SetLockedAction.class, source = SetLockedActionDto.class)
    @SubclassMapping(target = SetPauseAction.class, source = SetPauseActionDto.class)
    @SubclassMapping(target = ShufflePlayersAction.class, source = ShufflePlayersActionDto.class)
    @SubclassMapping(target = SubmitWordAction.class, source = SubmitWordActionDto.class)
    @SubclassMapping(target = SkipTurnAction.class, source = SkipTurnActionDto.class)
    CrossWardAction map(CrossWardActionDto actionDto);

    GetStateAction map(GetStateActionDto value);

    StartGameAction map(StartGameActionDto value);

    SetLockedAction map(SetLockedActionDto value);

    SetPauseAction map(SetPauseActionDto value);

    ShufflePlayersAction map(ShufflePlayersActionDto value);

    SkipTurnAction map(SkipTurnActionDto value);
}
