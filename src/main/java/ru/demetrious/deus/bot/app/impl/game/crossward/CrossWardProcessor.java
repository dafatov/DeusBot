package ru.demetrious.deus.bot.app.impl.game.crossward;

import java.util.concurrent.ExecutorService;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.Processor;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting;

import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@Component
public class CrossWardProcessor extends Processor<CrossWardInstance, CrossWardSetting, CrossWardPlayer, CrossWardActionContext, CrossWardAction> {
    protected CrossWardProcessor(NotifyGameStateOutbound notifyGameStateOutbound,
                                 ExecutorService virtualThreadPerTaskExecutor,
                                 GetGamePackWordsOutbound getGamePackWordsOutbound) {
        super(notifyGameStateOutbound,
            CrossWardAction.class,
            CrossWardSetting.class,
            new CrossWardActionContext(getGamePackWordsOutbound, notifyGameStateOutbound, virtualThreadPerTaskExecutor));
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
    public CrossWardPlayer createNewPlayer(String id, String name, String avatar) {
        return new CrossWardPlayer(id, name, avatar);
    }
}
