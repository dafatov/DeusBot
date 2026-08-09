package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetSpectatorAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.stream;
import static ru.demetrious.deus.bot.app.impl.game.codenames.utils.CrosswordUtils.placeWord;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateAction.class, name = "get_state"),
    @Type(value = StartGameAction.class, name = "start_game"),
    @Type(value = SetSpectatorAction.class, name = "set_spectator"),
    @Type(value = SetLockedAction.class, name = "set_locked"),
    @Type(value = SetPauseAction.class, name = "set_pause"),
    @Type(value = ShufflePlayersAction.class, name = "shuffle_players"),
})
public interface CrossWardAction extends Action<CrossWardSetting, CrossWardPlayer, CrossWardInstance, CrossWardActionContext> {
    @Override
    default String getGame() {
        return CROSS_WARD;
    }

    static void endPlayerPhase(CrossWardInstance gameSession, CrossWardActionContext ctx) {
        gameSession.getState().setCurrentPlayer((gameSession.getState().getCurrentPlayer() + 1) % gameSession.getPlayerList().size());
        placeWord(gameSession, gameSession.getPlayerList().get(gameSession.getState().getCurrentPlayer()));
        ctx.startTimer(gameSession, ofSeconds(2), () -> endPlayerPhase(gameSession, ctx));
    }

    static void checkLocked(CrossWardInstance gameSession) throws ActionException {
        if (gameSession.getState().isLocked()) {
            throw new ActionException("Game is locked");
        }
    }

    static void checkPhase(CrossWardInstance gameSession, State.Phase... phases) throws ActionException {
        if (stream(phases).noneMatch(phase -> phase == gameSession.getState().getPhase())) {
            throw new ActionException("Add hint can be only on HINTING phase");
        }
    }

    static void checkHost(CrossWardInstance gameSession, String userId) throws ActionException {
        if (!gameSession.getHostId().equals(userId)) {
            throw new ActionException("Only host can start game");
        }
    }

    static void checkPlayers(CrossWardInstance gameSession) throws ActionException {
        if (gameSession.getPlayerList().stream().allMatch(CrossWardPlayer::isSpectator)) {
            throw new ActionException("No players in this game");
        }
    }
}
