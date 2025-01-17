package ru.demetrious.deus.bot.app.api.user;

import ru.demetrious.deus.bot.domain.LinkUser;

@FunctionalInterface
public interface SaveLinkUserOutbound {
    void save(LinkUser linkUser);
}
