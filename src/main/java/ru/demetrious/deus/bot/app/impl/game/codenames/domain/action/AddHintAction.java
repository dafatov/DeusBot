package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Hint;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkTeamCaptain;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.endHintingPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.HINTING;

@Builder
public record AddHintAction(String word, int count) implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, HINTING);
        checkTeamCaptain(gameSession, player);

        gameSession.getHintList().add(new Hint(word, gameSession.getState().getTeam(), count));
        endHintingPhase(gameSession, ctx);
    }
}
