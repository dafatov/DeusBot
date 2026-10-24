package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import org.jetbrains.annotations.Nullable;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SetSpectatorAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.SkipTurnAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.UseSpellAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.function.Failable.asRunnable;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.TARGET_SCORE;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateAction.class, name = "get_state"),
    @Type(value = StartGameAction.class, name = "start_game"),
    @Type(value = SetSpectatorAction.class, name = "set_spectator"),
    @Type(value = SetLockedAction.class, name = "set_locked"),
    @Type(value = SetPauseAction.class, name = "set_pause"),
    @Type(value = ShufflePlayersAction.class, name = "shuffle_players"),
    @Type(value = SkipTurnAction.class, name = "skip_turn"),
    @Type(value = UseSpellAction.class, name = "use_spell"),
})
public interface CrossWardAction extends Action<CrossWardPlayer, CrossWardInstance, CrossWardActionContext, CrossWardAction> {
    @Override
    default String getGame() {
        return CROSS_WARD;
    }

    static void endPlayerPhase(CrossWardInstance gameSession, CrossWardActionContext ctx) throws ActionException {
        checkPhase(gameSession, PLAYING);

        int currentIndex = gameSession.getActivePlayers().indexOf(gameSession.getState().getCurrentPlayer());
        int nextIndex = (currentIndex + 1) % gameSession.getActivePlayers().size();

        gameSession.getState().setCurrentPlayer(gameSession.getActivePlayers().get(nextIndex));
        gameSession.getState().setCurrentEnergy(2);
        gameSession.getState().setCurrentStreak(0);
        gameSession.placeWord();
        ctx.startTimer(gameSession, ofMinutes(2), asRunnable(() -> endPlayerPhase(gameSession, ctx)));
    }

    static boolean tryFinishGame(CrossWardInstance gameSession, CrossWardActionContext ctx, @Nullable CrossWardPlayer player) {
        if (isNull(player)) {
            gameSession.getState().setCurrentPlayer(null);
            gameSession.getState().setPhase(FINISHED);
            ctx.cancelTimer(gameSession.getTimer());
            return true;
        }

        if (player.getScore() < TARGET_SCORE) {
            return false;
        }

        gameSession.getState().setCurrentPlayer(player);
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


    static void checkTurn(CrossWardInstance gameSession, CrossWardPlayer player) throws ActionException {
        if (!gameSession.getState().getCurrentPlayer().equals(player)) {
            throw new ActionException("Wrong player in this action");
        }
    }

    static void checkHost(CrossWardInstance gameSession, CrossWardPlayer player) throws ActionException {
        if (!gameSession.getHostId().equals(player.getId())) {
            throw new ActionException("Only host can start game");
        }
    }

    static void checkPlayers(CrossWardInstance gameSession) throws ActionException {
        if (gameSession.getActivePlayers().isEmpty()) {
            throw new ActionException("No players in this game");
        }
    }
}
