package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.shuffle;
import static java.util.concurrent.ThreadLocalRandom.current;
import static ru.demetrious.deus.bot.app.impl.game.codenames.utils.CrosswordUtils.placeWord;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPlayers;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;

@Slf4j
@Builder
public record StartGameAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, userId);
        checkPlayers(gameSession);

        startBoard(gameSession, ctx);
        gameSession.getState().setPhase(PLAYING);
        gameSession.getState().setLocked(true);
        gameSession.getState().setCurrentPlayer(0);
        gameSession.getPlayerList().forEach(player -> player.setScore(0));
        ctx.startTimer(gameSession, ofSeconds(2), () -> endPlayerPhase(gameSession, ctx));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static void startBoard(CrossWardInstance gameSession, CrossWardActionContext ctx) {
        List<String> words = new ArrayList<>(ctx.getDictionary().getWords(gameSession.getSetting().packId()));

        shuffle(words, current());
        gameSession.getAvailableWords().clear();
        gameSession.getAvailableWords().addAll(words);
        gameSession.getWords().clear();
        gameSession.getGrid().clear();

        placeWord(gameSession, 3, null);
    }
}
