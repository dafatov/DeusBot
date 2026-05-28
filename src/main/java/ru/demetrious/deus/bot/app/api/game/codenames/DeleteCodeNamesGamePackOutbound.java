package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface DeleteCodeNamesGamePackOutbound {
    void deletePack(Long id);
}
