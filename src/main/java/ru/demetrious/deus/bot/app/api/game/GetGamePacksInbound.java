package ru.demetrious.deus.bot.app.api.game;

import java.util.List;
import ru.demetrious.deus.bot.domain.game.Pack;

@FunctionalInterface
public interface GetGamePacksInbound {
    List<Pack> execute();
}
