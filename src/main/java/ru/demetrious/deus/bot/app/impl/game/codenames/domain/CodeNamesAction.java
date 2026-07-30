package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import java.util.Optional;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.AddHintAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ChangeTeamAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetHintGuessedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetLockedAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.SetPauseAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.ShufflePlayersAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.StartGameAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.VoteAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Hint;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.HINTING;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

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
public interface CodeNamesAction extends Action<CodeNamesSetting, CodeNamesPlayer, CodeNamesInstance, CodeNamesActionContext> {
    @Override
    default String getGame() {
        return CODE_NAMES;
    }

    static void endHintingPhaseTimeout(CodeNamesInstance gameSession, CodeNamesActionContext ctx) {
        gameSession.getHintList().add(new Hint("-", gameSession.getState().getTeam(), 0));
        endHintingPhase(gameSession, ctx);
    }

    static void endHintingPhase(CodeNamesInstance gameSession, CodeNamesActionContext ctx) {
        gameSession.getState().setPhase(GUESSING);
        ctx.startTimer(gameSession, ofMinutes(1), () -> endGuessingPhase(gameSession, ctx));
    }

    static void endGuessingPhase(CodeNamesInstance gameSession, CodeNamesActionContext ctx) {
        gameSession.getState().setPhase(HINTING);
        gameSession.getState().setTeam(gameSession.getState().getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
        gameSession.getState().setRound(gameSession.getState().getRound() + 1);
        ctx.startTimer(gameSession, ofMinutes(1), () -> endHintingPhaseTimeout(gameSession, ctx));
    }

    static void checkLocked(CodeNamesInstance gameSession) throws ActionException {
        if (gameSession.getState().isLocked()) {
            throw new ActionException("Game is locked");
        }
    }

    static void checkHost(CodeNamesInstance gameSession, String userId) throws ActionException {
        if (!gameSession.getHostId().equals(userId)) {
            throw new ActionException("Only host can start game");
        }
    }

    static void checkTeamCaptain(CodeNamesInstance gameSession, String userId) throws ActionException {
        Optional<CodeNamesPlayer> captain = gameSession.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId) && p.isCaptain() && p.getTeam() == gameSession.getState().getTeam())
            .findFirst();

        if (captain.isEmpty()) {
            throw new ActionException("Only captain can add hint game");
        }
    }

    static void checkTeamMate(CodeNamesInstance gameSession, String userId, Team team) throws ActionException {
        Optional<CodeNamesPlayer> mate = gameSession.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId) && !p.isCaptain() && p.getTeam() == team)
            .findFirst();

        if (mate.isEmpty()) {
            throw new ActionException("Only mate can guess hint game");
        }
    }

    static void checkPaused(CodeNamesInstance gameSession) throws ActionException {
        if (nonNull(gameSession.getTimer().getRemaining())) {
            throw new ActionException("Game is paused");
        }
    }

    static void checkPhase(CodeNamesInstance gameSession, Phase... phases) throws ActionException {
        if (stream(phases).noneMatch(phase -> phase == gameSession.getState().getPhase())) {
            throw new ActionException("Add hint can be only on HINTING phase");
        }
    }
}
