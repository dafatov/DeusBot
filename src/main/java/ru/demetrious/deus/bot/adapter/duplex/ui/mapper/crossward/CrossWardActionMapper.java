package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.GetStateActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.EchoSpellDto;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetSpectatorAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SkipTurnAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SubmitWordAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.UseSpellAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.CrosslightSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.CrucifixSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.EchoSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.LonerSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.RadarSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetLockedActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetPauseActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SetSpectatorActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.ShufflePlayersActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SkipTurnActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.StartGameActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.SubmitWordActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.UseSpellActionDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.CrosslightSpellDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.CrucifixDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.LonerSpellDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.RadarSpellDto;

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
    @SubclassMapping(target = UseSpellAction.class, source = UseSpellActionDto.class)
    CrossWardAction map(CrossWardActionDto actionDto);

    GetStateAction mapGetState(GetStateActionDto value);

    StartGameAction mapStartGame(StartGameActionDto value);

    SetLockedAction mapSetLocked(SetLockedActionDto value);

    SetPauseAction mapSetPause(SetPauseActionDto value);

    ShufflePlayersAction mapShufflePlayers(ShufflePlayersActionDto value);

    SkipTurnAction mapSkipTurn(SkipTurnActionDto value);

    @SubclassMapping(target = RadarSpell.class, source = RadarSpellDto.class)
    @SubclassMapping(target = EchoSpell.class, source = EchoSpellDto.class)
    @SubclassMapping(target = LonerSpell.class, source = LonerSpellDto.class)
    @SubclassMapping(target = CrosslightSpell.class, source = CrosslightSpellDto.class)
    @SubclassMapping(target = CrucifixSpell.class, source = CrucifixDto.class)
    Spell map(SpellDto value);

    // =========================================================================================================================================================

    @InheritInverseConfiguration
    CrossWardActionDto map(CrossWardAction action);

    GetStateActionDto mapGetState(GetStateAction value);

    StartGameActionDto mapStartGame(StartGameAction value);

    SetLockedActionDto mapSetLocked(SetLockedAction value);

    SetPauseActionDto mapSetPause(SetPauseAction value);

    ShufflePlayersActionDto mapShufflePlayers(ShufflePlayersAction value);

    SkipTurnActionDto mapSkipTurn(SkipTurnAction value);

    @InheritInverseConfiguration
    SpellDto map(Spell spell);
}
