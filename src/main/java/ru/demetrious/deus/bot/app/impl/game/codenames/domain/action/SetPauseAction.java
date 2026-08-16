package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static java.util.Objects.isNull;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.GUESSING;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.HINTING;

@Builder
public record SetPauseAction() implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, player);
        checkPhase(gameSession, GUESSING, HINTING);

        if (isNull(gameSession.getTimer().getRemaining())) {
            ctx.pauseTimer(gameSession.getTimer());
        } else {
            ctx.resumeTimer(gameSession);
        }
    }
}
