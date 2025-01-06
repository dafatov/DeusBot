package ru.demetrious.deus.bot.app.api.session;

import java.util.Optional;
import ru.demetrious.deus.bot.domain.Session;

@FunctionalInterface
public interface GetSessionOutbound {
    Optional<Session> getSession(Session.SessionId sessionId);
}
