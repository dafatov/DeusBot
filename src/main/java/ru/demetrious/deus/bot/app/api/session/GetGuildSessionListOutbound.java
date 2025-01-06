package ru.demetrious.deus.bot.app.api.session;

import java.util.List;
import ru.demetrious.deus.bot.domain.Session;

@FunctionalInterface
public interface GetGuildSessionListOutbound {
    List<Session> getGuildSessionList(String guildId);
}
