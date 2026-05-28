package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Hint;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.HINTING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkTeamCaptain;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.endHintingPhase;

@Builder
public record AddHintAction(String word, int count) implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, HINTING);
        checkTeamCaptain(gameSession, userId);

        gameSession.getHintList().add(new Hint(word, gameSession.getState().getTeam(), count));
        endHintingPhase(gameSession, ctx);
    }
}
