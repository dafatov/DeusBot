package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import java.util.Map;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Word;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.Context.Timer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote;

import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkTeamMate;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.endGuessingPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.WordVote;

@Builder
public record VoteAction(Vote vote) implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, GUESSING);
        checkTeamMate(gameSession, userId, gameSession.getState().getTeam());

        updateVotes(gameSession.getVoteMap(), userId);

        if (!isAllVotesCompatible(gameSession)) {
            return;
        }

        resolveVoting(gameSession, userId, ctx);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void updateVotes(Map<String, Vote> voteMap, String userId) {
        voteMap.compute(userId, (_, previous) -> switch (vote) {
            case WordVote v -> isNull(previous) || !(previous instanceof WordVote(String word)) || !StringUtils.equals(word, v.word())
                ? new WordVote(v.word())
                : null;
            case SkipVote _ -> isNull(previous) ? new SkipVote() : null;
            default -> throw new IllegalStateException("Unexpected vote value: " + vote);
        });
    }

    private static boolean isAllVotesCompatible(GameSession gameSession) {
        return gameSession.getPlayerList().stream()
            .filter(p1 -> !p1.isCaptain() && p1.getTeam() == gameSession.getState().getTeam())
            .allMatch(p -> gameSession.getVoteMap().containsKey(p.getId()))
            && (gameSession.getVoteMap().values().stream().allMatch(SkipVote.class::isInstance)
            || gameSession.getVoteMap().values().stream().allMatch(WordVote.class::isInstance)
            && gameSession.getVoteMap().values().stream().map(WordVote.class::cast).map(WordVote::word).distinct().count() == 1);
    }

    private static void resolveVoting(GameSession gameSession, String userId, Context ctx) {
        Vote vote = gameSession.getVoteMap().values().iterator().next();
        Boolean needSkipPhase = switch (vote) {
            case SkipVote _ -> true;
            case WordVote v -> resolveWordVoting(gameSession, userId, ctx, v);
            default -> throw new IllegalStateException("Unexpected vote value: " + vote);
        };

        gameSession.getVoteMap().clear();
        if (isTrue(needSkipPhase)) {
            endGuessingPhase(gameSession, ctx);
        }
    }

    private static Boolean resolveWordVoting(GameSession gameSession, String userId, Context ctx, WordVote vote) {
        Word word = gameSession.getWordList().stream()
            .filter(w -> StringUtils.equals(w.getText(), vote.word()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Can't find word that voted"));

        word.setRevealed(true);
        return switch (word.getColor()) {
            case BLACK -> {
                finishGame(gameSession, gameSession.getPlayerList().stream()
                    .filter(p -> p.getId().equals(userId))
                    .findFirst()
                    .orElseThrow()
                    .getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
                yield null;
            }
            case RED -> handleColoredWord(gameSession, ctx, Team.RED);
            case BLUE -> handleColoredWord(gameSession, ctx, Team.BLUE);
            case WHITE -> true;
        };
    }

    private static Boolean handleColoredWord(GameSession gameSession, Context ctx, Team team) {
        gameSession.getState().getScore().remove(team);
        if (!gameSession.getState().getScore().contains(team)) {
            finishGame(gameSession, team);
            return null;
        }

        boolean isSameTeam = gameSession.getState().getTeam() == team;

        if (isSameTeam) {
            ctx.timerSetter().accept(new Timer(gameSession,
                between(now(), gameSession.getState().getTimer()).plus(ofSeconds(10)),
                gameSession.getState().getTimerTask()));
        }

        return !isSameTeam;
    }

    private static void finishGame(GameSession gameSession, Team team) {
        gameSession.getState().setTeam(team);
        gameSession.getState().setPhase(FINISHED);
        gameSession.getState().getTimerCompletableFuture().cancel(true);
        gameSession.getState().setTimerTask(null);
        gameSession.getState().setTimer(null);
        gameSession.getState().setTimerCompletableFuture(null);
    }
}
