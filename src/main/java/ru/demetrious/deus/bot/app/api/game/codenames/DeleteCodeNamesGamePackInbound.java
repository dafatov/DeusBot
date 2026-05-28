package ru.demetrious.deus.bot.app.api.game.codenames;

@FunctionalInterface
public interface DeleteCodeNamesGamePackInbound {
    void execute(Long id);
}
