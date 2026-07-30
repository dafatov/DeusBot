package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import java.util.concurrent.ExecutorService;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionContext;

public final class CodeNamesActionContext extends ActionContext<CodeNamesSetting, CodeNamesPlayer, CodeNamesInstance> {
    public CodeNamesActionContext(GetGamePackWordsOutbound dictionary,
                                  NotifyGameStateOutbound notifyGameStateOutbound,
                                  ExecutorService virtualThreadPerTaskExecutor) {
        super(dictionary, notifyGameStateOutbound, virtualThreadPerTaskExecutor);
    }
}
