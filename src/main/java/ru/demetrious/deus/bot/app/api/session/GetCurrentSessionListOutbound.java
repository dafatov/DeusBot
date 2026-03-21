package ru.demetrious.deus.bot.app.api.session;

import java.util.Set;
import ru.demetrious.deus.bot.domain.Session.SessionId;

public interface GetCurrentSessionListOutbound {
    Set<SessionId> getCurrentSessionList();
}
