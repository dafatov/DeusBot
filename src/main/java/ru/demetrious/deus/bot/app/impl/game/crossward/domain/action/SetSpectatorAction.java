package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.tryFinishGame;

public record SetSpectatorAction(boolean spectator) implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx, ActionEvent<CrossWardAction, CrossWardPlayer> event) throws ActionException {
        checkLocked(gameSession);

        if (spectator) {
            player.setSpectator(true);
            player.setScore(0);
            gameSession.getActivePlayers().remove(player);
            gameSession.getWords().stream()
                .filter(f -> player.equals(f.getOwner()))
                .forEach(word -> word.setOwner(null));
            if (gameSession.getState().getCurrentPlayer().equals(player)) {
                endPlayerPhase(gameSession, ctx);
            }
        } else {
            player.setSpectator(false);
            gameSession.getActivePlayers().add(player);
        }

        if (gameSession.getActivePlayers().isEmpty()) {
            tryFinishGame(gameSession, ctx, null);
        }
    }
}
