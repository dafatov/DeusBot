package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Hint;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.Context.Timer;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.time.Duration.ofMinutes;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.HINTING;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = GetStateAction.class, name = "get_state"),
    @Type(value = ChangeTeamAction.class, name = "change_team"),
    @Type(value = StartGameAction.class, name = "start_game"),
    @Type(value = AddHintAction.class, name = "add_hint"),
    @Type(value = SetHintGuessedAction.class, name = "set_hint_guessed"),
    @Type(value = VoteAction.class, name = "vote"),
    @Type(value = ShufflePlayersAction.class, name = "shuffle_players"),
    @Type(value = SetLockedAction.class, name = "set_locked"),
    @Type(value = SetPauseAction.class, name = "set_pause"),
})
public interface Action {
    void perform(GameSession gameSession, String userId, Context ctx) throws ActionException;

    record Context(Consumer<Timer> timerSetter, GetCodeNamesGamePackWordsOutbound dictionary) {
        public record Timer(GameSession gameSession, Duration delay, Runnable task) {
        }
    }

    class ActionException extends Exception {
        public ActionException(String detailMessage) {
            super(detailMessage);
        }
    }

    static void endHintingPhaseTimeout(GameSession gameSession, Context ctx) {
        gameSession.getHintList().add(new Hint("-", gameSession.getState().getTeam(), 0));
        endHintingPhase(gameSession, ctx);
    }

    static void endHintingPhase(GameSession gameSession, Context ctx) {
        gameSession.getState().setPhase(GUESSING);
        ctx.timerSetter.accept(new Timer(gameSession, ofMinutes(1), () -> endGuessingPhase(gameSession, ctx)));
    }

    static void endGuessingPhase(GameSession gameSession, Context ctx) {
        gameSession.getState().setPhase(HINTING);
        gameSession.getState().setTeam(gameSession.getState().getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
        ctx.timerSetter.accept(new Timer(gameSession, ofMinutes(1), () -> endHintingPhaseTimeout(gameSession, ctx)));
    }

    static void checkLocked(GameSession gameSession) throws ActionException {
        if (gameSession.getState().isLocked()) {
            throw new ActionException("Game is locked");
        }
    }

    static void checkHost(GameSession gameSession, String userId) throws ActionException {
        if (!gameSession.getHostId().equals(userId)) {
            throw new ActionException("Only host can start game");
        }
    }

    static void checkTeamCaptain(GameSession gameSession, String userId) throws ActionException {
        Optional<Player> captain = gameSession.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId) && p.isCaptain() && p.getTeam() == gameSession.getState().getTeam())
            .findFirst();

        if (captain.isEmpty()) {
            throw new ActionException("Only captain can add hint game");
        }
    }

    static void checkTeamMate(GameSession gameSession, String userId, Team team) throws ActionException {
        Optional<Player> mate = gameSession.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId) && !p.isCaptain() && p.getTeam() == team)
            .findFirst();

        if (mate.isEmpty()) {
            throw new ActionException("Only mate can guess hint game");
        }
    }

    static void checkPaused(GameSession gameSession) throws ActionException {
        if (nonNull(gameSession.getState().getRemaining())) {
            throw new ActionException("Game is paused");
        }
    }

    static void checkPhase(GameSession gameSession, Phase... phases) throws ActionException {
        if (stream(phases).noneMatch(phase -> phase == gameSession.getState().getPhase())) {
            throw new ActionException("Add hint can be only on HINTING phase");
        }
    }
}
