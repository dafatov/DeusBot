package ru.demetrious.deus.bot.app.api.game.codenames;

import ru.demetrious.deus.bot.domain.game.Pack;

@FunctionalInterface
public interface SaveCodeNamesGamePacksOutbound {
    void savePack(Pack pack);
}
