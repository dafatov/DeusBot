package ru.demetrious.deus.bot.app.impl.game.codenames.api;

import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Setting;

public interface Gamebox {
    String createNewGame(Setting setting);

    void joinGame(String gameId);

    GameSession getGameSession(String gameId, String userId);

    Optional<Pair<GameSession, Player>> findByPlayer(String userId);

    void removeGame(String gameId);
}
