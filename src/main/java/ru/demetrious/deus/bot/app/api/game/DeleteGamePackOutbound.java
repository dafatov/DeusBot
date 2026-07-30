package ru.demetrious.deus.bot.app.api.game;

@FunctionalInterface
public interface DeleteGamePackOutbound {
    void deletePack(Long id);
}
