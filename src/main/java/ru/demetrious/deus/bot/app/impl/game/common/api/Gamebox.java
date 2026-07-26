package ru.demetrious.deus.bot.app.impl.game.common.api;

import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

public interface Gamebox {
    String createNewGame(Setting setting);

    void joinGame(String gameId);

    Optional<? extends Pair<? extends Instance<?, ?>, ? extends Player>> findByPlayer(String userId);

    void performAction(String gameId, String userId, Action<?, ?, ?, ?> action) throws ActionException;

    void removeGame(String gameId);
}
