package ru.demetrious.deus.bot.app.api.session;

import ru.demetrious.deus.bot.domain.Session;

@FunctionalInterface
public interface SaveSessionOutbound {
    void saveSession(Session session);
}
