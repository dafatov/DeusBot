package ru.demetrious.deus.bot.app.api.game;

import ru.demetrious.deus.bot.domain.game.Pack;

@FunctionalInterface
public interface SaveGamePacksOutbound {
    void savePack(Pack pack);
}
