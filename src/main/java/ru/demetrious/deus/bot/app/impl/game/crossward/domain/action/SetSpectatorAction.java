package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.awt.Color;
import java.util.List;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.sqrt;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.tryFinishGame;

public record SetSpectatorAction(boolean spectator) implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);

        if (spectator) {
            player.setSpectator(true);
            player.setScore(0);
            player.setColor(null);
            gameSession.getActivePlayers().remove(player);
            gameSession.getWords().stream()
                .filter(f -> player.equals(f.getOwner()))
                .forEach(word -> word.setOwner(null));
        } else {
            player.setSpectator(false);
            gameSession.getActivePlayers().add(player);
        }

        List<CrossWardPlayer> activePlayers = gameSession.getActivePlayers();
        for (int i = 0; i < activePlayers.size(); i++) {
            activePlayers.get(i).setColor(generateUniqueColor(i, gameSession.getKey().hashCode()));
        }

        if (gameSession.getActivePlayers().isEmpty()) {
            tryFinishGame(gameSession, ctx, null);
        }
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Color generateUniqueColor(int index, int seed) {
        final double goldenRatio = (sqrt(5) - 1) / 2;
        float hue = (float) ((index * goldenRatio + seed * 0.618) % 1.0);
        float saturation = 0.85f;
        float brightness = 0.65f;

        return getHSBColor(hue, saturation, brightness);
    }
}
