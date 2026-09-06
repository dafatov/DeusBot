package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.util.concurrent.ExecutorService;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionContext;

public final class CrossWardActionContext extends ActionContext<CrossWardInstance> {
    public CrossWardActionContext(GetGamePackWordsOutbound dictionary,
                                  NotifyGameStateOutbound notifyGameStateOutbound,
                                  ExecutorService virtualThreadPerTaskExecutor) {
        super(dictionary, notifyGameStateOutbound, virtualThreadPerTaskExecutor);
    }
}
