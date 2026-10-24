package ru.demetrious.deus.bot.app.impl.game.crossward;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.Processor;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.sqrt;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@Component
public class CrossWardProcessor extends Processor<CrossWardInstance, CrossWardSetting, CrossWardPlayer, CrossWardActionContext, CrossWardAction> {
    private final CrossWardActionContext context;

    protected CrossWardProcessor(NotifyGameStateOutbound notifyGameStateOutbound,
                                 ExecutorService virtualThreadPerTaskExecutor,
                                 GetGamePackWordsOutbound getGamePackWordsOutbound) {
        CrossWardActionContext context = new CrossWardActionContext(getGamePackWordsOutbound, notifyGameStateOutbound, virtualThreadPerTaskExecutor);

        this.context = context;
        super(notifyGameStateOutbound,
            CrossWardAction.class,
            CrossWardSetting.class,
            context);
    }

    @Override
    public String getGame() {
        return CROSS_WARD;
    }

    @Override
    public CrossWardInstance createNewGame(String key, String hostId, CrossWardSetting setting) {
        return new CrossWardInstance(key, hostId, setting);
    }

    @Override
    public CrossWardPlayer createNewPlayer(CrossWardInstance game, String id, String name, String avatar) {
        return new CrossWardPlayer(id, name, avatar, generateUniqueColor(id.hashCode(), game.getKey().hashCode()));
    }

    @Override
    public void onPlayerDisconnect(CrossWardInstance game, CrossWardPlayer player) throws ActionException {
        if (player.equals(game.getState().getCurrentPlayer())) {
            endPlayerPhase(game, context);
        }

        game.getWords().stream()
            .filter(f -> player.equals(f.getOwner()))
            .forEach(word -> word.setOwner(null));
        super.onPlayerDisconnect(game, player);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Color generateUniqueColor(int playerHash, int gameHash) {
        final double goldenRatio = (sqrt(5) - 1) / 2;
        float hue = (float) ((playerHash * goldenRatio + gameHash * 0.618) % 1.0);
        float saturation = 0.85f;
        float brightness = 0.45f;

        return getHSBColor(hue, saturation, brightness);
    }
}
