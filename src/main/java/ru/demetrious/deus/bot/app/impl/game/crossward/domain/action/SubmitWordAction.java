package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static java.time.Duration.ofSeconds;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkTurn;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.tryFinishGame;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.SCORE_COEFFICIENT_FUNCTION;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;


@Builder
public record SubmitWordAction(int wordId, String word) implements CrossWardAction {

    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, PLAYING);
        checkTurn(gameSession, player);

        Word word = gameSession.getWords().stream()
            .filter(g -> g.getOrder() == wordId)
            .findFirst()
            .orElseThrow(() -> new ActionException("Word not found"));

        if (!equalsIgnoreCase(word.getText(), this.word)) {
            endPlayerPhase(gameSession, ctx);
            return;
        }

        player.setScore(player.getScore() + word.reveal(SCORE_COEFFICIENT_FUNCTION.apply(player)));
        if (tryFinishGame(gameSession, ctx, player)) {
            return;
        }
        ctx.extendTimer(gameSession, ofSeconds(10));
    }
}
