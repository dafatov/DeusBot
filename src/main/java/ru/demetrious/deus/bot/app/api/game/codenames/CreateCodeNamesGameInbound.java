package ru.demetrious.deus.bot.app.api.game.codenames;

import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Setting;

@FunctionalInterface
public interface CreateCodeNamesGameInbound {
    String execute(Setting setting);
}
