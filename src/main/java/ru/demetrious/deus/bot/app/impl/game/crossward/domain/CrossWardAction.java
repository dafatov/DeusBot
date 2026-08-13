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
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SkipTurnAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.function.Failable.asRunnable;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.TARGET_SCORE;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;
import static ru.demetrious.deus.bot.app.impl.game.crossward.utils.CrosswordUtils.placeWord;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateAction.class, name = "get_state"),
    @Type(value = StartGameAction.class, name = "start_game"),
    @Type(value = SetSpectatorAction.class, name = "set_spectator"),
    @Type(value = SetLockedAction.class, name = "set_locked"),
    @Type(value = SetPauseAction.class, name = "set_pause"),
    @Type(value = ShufflePlayersAction.class, name = "shuffle_players"),
    @Type(value = SkipTurnAction.class, name = "skip_turn"),
})
public interface CrossWardAction extends Action<CrossWardSetting, CrossWardPlayer, CrossWardInstance, CrossWardActionContext> {
    @Override
    default String getGame() {
        return CROSS_WARD;
    }

    static void endPlayerPhase(CrossWardInstance gameSession, CrossWardActionContext ctx) throws ActionException {
        checkPhase(gameSession, PLAYING);

        gameSession.getState().setCurrentPlayer((gameSession.getState().getCurrentPlayer() + 1) % gameSession.getPlayerList().size());
        //TODO переделать так при текущем подходе не учитывается что может не быть игроков + что игрок может быть в спектаторах
        placeWord(gameSession, gameSession.getPlayerList().get(gameSession.getState().getCurrentPlayer()));
        ctx.startTimer(gameSession, ofMinutes(2), asRunnable(() -> endPlayerPhase(gameSession, ctx)));
    }

    //TODO после исправления текущего игрока исправить и это
    static boolean tryFinishGame(CrossWardInstance gameSession, CrossWardActionContext ctx, CrossWardPlayer player) {
        if (player.getScore() < TARGET_SCORE) {
            return false;
        }

        gameSession.getState().setCurrentPlayer(0);
        gameSession.getState().setPhase(FINISHED);
        ctx.cancelTimer(gameSession.getTimer());
        return true;
    }

    static void checkLocked(CrossWardInstance gameSession) throws ActionException {
        if (gameSession.getState().isLocked()) {
            throw new ActionException("Game is locked");
        }
    }

    static void checkPaused(CrossWardInstance gameSession) throws ActionException {
        if (nonNull(gameSession.getTimer().getRemaining())) {
            throw new ActionException("Game is paused");
        }
    }

    static void checkPhase(CrossWardInstance gameSession, State.Phase... phases) throws ActionException {
        if (stream(phases).noneMatch(phase -> phase == gameSession.getState().getPhase())) {
            throw new ActionException("Incorrect phase for this action");
        }
    }


    static void checkTurn(CrossWardInstance gameSession, String userId) throws ActionException {
        if (!gameSession.getPlayerList().get(gameSession.getState().getCurrentPlayer()).getId().equals(userId)) {
            throw new ActionException("Wrong player in this action");
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
