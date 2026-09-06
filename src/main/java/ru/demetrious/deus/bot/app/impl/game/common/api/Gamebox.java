package ru.demetrious.deus.bot.app.impl.game.common.api;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

public interface Gamebox {
    String createNewGame(Setting setting);

    void joinGame(String gameId);

    void connect(String userId);

    void disconnect(String userId);

    void performAction(String gameId, String userId, Action<?, ?, ?, ?> action) throws ActionException;
}
