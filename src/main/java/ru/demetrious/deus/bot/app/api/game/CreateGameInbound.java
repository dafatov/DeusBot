package ru.demetrious.deus.bot.app.api.game;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

@FunctionalInterface
public interface CreateGameInbound {
    String execute(Setting setting);
}
