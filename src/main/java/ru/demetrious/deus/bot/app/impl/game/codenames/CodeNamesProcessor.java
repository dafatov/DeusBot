package ru.demetrious.deus.bot.app.impl.game.codenames;

import java.util.concurrent.ExecutorService;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.GetGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesSetting;
import ru.demetrious.deus.bot.app.impl.game.common.Processor;

import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

@Component
public class CodeNamesProcessor extends Processor<CodeNamesInstance, CodeNamesSetting, CodeNamesPlayer, CodeNamesActionContext, CodeNamesAction> {
    protected CodeNamesProcessor(NotifyGameStateOutbound notifyGameStateOutbound,
                                 ExecutorService virtualThreadPerTaskExecutor,
                                 GetGamePackWordsOutbound getGamePackWordsOutbound) {
        super(notifyGameStateOutbound,
            CodeNamesAction.class,
            CodeNamesSetting.class,
            new CodeNamesActionContext(getGamePackWordsOutbound, notifyGameStateOutbound, virtualThreadPerTaskExecutor));
    }

    @Override
    public String getGame() {
        return CODE_NAMES;
    }

    @Override
    public CodeNamesInstance createNewGame(String key, String hostId, CodeNamesSetting setting) {
        return new CodeNamesInstance(key, hostId, setting);
    }

    @Override
    public CodeNamesPlayer createNewPlayer(String id, String name, String avatar) {
        return new CodeNamesPlayer(id, name, avatar);
    }
}
