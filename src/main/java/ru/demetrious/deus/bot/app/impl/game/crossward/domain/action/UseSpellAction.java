package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkTurn;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;

@Slf4j
@Builder
public record UseSpellAction(Spell spell) implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        checkPaused(gameSession);
        checkPhase(gameSession, PLAYING);
        checkTurn(gameSession, player);
        checkSpellCost(gameSession, spell);

        boolean needEndTurn = spell.use(gameSession, player, ctx);

        gameSession.getState().setCurrentEnergy(gameSession.getState().getCurrentEnergy() - spell.getCost());
        if (needEndTurn) {
            endPlayerPhase(gameSession, ctx);
        }
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static void checkSpellCost(CrossWardInstance gameSession, Spell spell) throws ActionException {
        if (gameSession.getState().getCurrentEnergy() < spell.getCost()) {
            throw new ActionException("Can't use this action because the current energy is too low");
        }
    }
}
