package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.awt.Color;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.sqrt;
import static java.util.stream.IntStream.range;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;

public record SetSpectatorAction(boolean spectator) implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);

        int index = range(0, gameSession.getPlayerList().size())
            .filter(i -> gameSession.getPlayerList().get(i).getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Player not found"));

        gameSession.getPlayerList().get(index)
            .setSpectator(spectator)
            .setColor(spectator ? null : generateUniqueColor(index))
            .setScore(0);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Color generateUniqueColor(int index) {
        final double goldenRatio = (sqrt(5) - 1) / 2;
        float hue = (float) ((index * goldenRatio) % 1.0);
        float saturation = 0.85f;
        float brightness = 0.9f;

        return getHSBColor(hue, saturation, brightness);
    }
}
