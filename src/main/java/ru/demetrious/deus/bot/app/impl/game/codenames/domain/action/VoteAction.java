package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word.Reveal;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkTeamMate;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.endGuessingPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.WordVote;

@Builder
public record VoteAction(Vote vote) implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, GUESSING);
        checkTeamMate(player, gameSession.getState().getTeam());

        updateVotes(gameSession.getVoteMap(), player);

        if (!isAllVotesCompatible(gameSession)) {
            return;
        }

        resolveVoting(gameSession, player, ctx);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void updateVotes(Map<String, Vote> voteMap, CodeNamesPlayer player) {
        voteMap.compute(player.getId(), (_, previous) -> switch (vote) {
            case WordVote v -> isNull(previous) || !(previous instanceof WordVote(String word)) || !StringUtils.equals(word, v.word())
                ? new WordVote(v.word())
                : null;
            case SkipVote _ -> isNull(previous) ? new SkipVote() : null;
            default -> throw new IllegalStateException("Unexpected vote value: " + vote);
        });
    }

    private static boolean isAllVotesCompatible(CodeNamesInstance gameSession) {
        return gameSession.getPlayerList().stream()
            .filter(p1 -> !p1.isCaptain() && p1.getTeam() == gameSession.getState().getTeam())
            .allMatch(p -> gameSession.getVoteMap().containsKey(p.getId()))
            && (gameSession.getVoteMap().values().stream().allMatch(SkipVote.class::isInstance)
            || gameSession.getVoteMap().values().stream().allMatch(WordVote.class::isInstance)
            && gameSession.getVoteMap().values().stream().map(WordVote.class::cast).map(WordVote::word).distinct().count() == 1);
    }

    private static void resolveVoting(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx) throws ActionException {
        Vote vote = gameSession.getVoteMap().values().iterator().next();
        Boolean needSkipPhase = switch (vote) {
            case SkipVote _ -> true;
            case WordVote v -> resolveWordVoting(gameSession, player, ctx, v);
            default -> throw new IllegalStateException("Unexpected vote value: " + vote);
        };

        gameSession.getVoteMap().clear();
        if (isTrue(needSkipPhase)) {
            endGuessingPhase(gameSession, ctx);
        }
    }

    private static Boolean resolveWordVoting(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx, WordVote vote) throws ActionException {
        Word word = gameSession.getWordList().stream()
            .filter(w -> StringUtils.equals(w.getText(), vote.word()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Can't find word that voted"));
        Integer previousOrder = gameSession.getWordList().stream()
            .map(Word::getRevealed)
            .filter(Objects::nonNull)
            .map(Reveal::order)
            .max(Integer::compareTo)
            .orElse(0);

        word.setRevealed(new Reveal(previousOrder + 1, gameSession.getState().getTeam(), gameSession.getState().getRound()));
        return switch (word.getColor()) {
            case BLACK -> {
                finishGame(gameSession, ctx, player.getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
                yield null;
            }
            case RED -> handleColoredWord(gameSession, ctx, Team.RED);
            case BLUE -> handleColoredWord(gameSession, ctx, Team.BLUE);
            case WHITE -> true;
        };
    }

    private static Boolean handleColoredWord(CodeNamesInstance gameSession, CodeNamesActionContext ctx, Team team) throws ActionException {
        gameSession.getState().getScore().remove(team);
        if (!gameSession.getState().getScore().contains(team)) {
            finishGame(gameSession, ctx, team);
            return null;
        }

        boolean isSameTeam = gameSession.getState().getTeam() == team;

        if (isSameTeam) {
            ctx.extendTimer(gameSession, ofSeconds(10));
        }

        return !isSameTeam;
    }

    private static void finishGame(CodeNamesInstance gameSession, CodeNamesActionContext ctx, Team team) {
        gameSession.getState().setTeam(team);
        gameSession.getState().setPhase(FINISHED);
        ctx.cancelTimer(gameSession.getTimer());
    }
}
