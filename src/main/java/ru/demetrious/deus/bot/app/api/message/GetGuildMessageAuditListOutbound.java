package ru.demetrious.deus.bot.app.api.message;

import java.util.List;
import ru.demetrious.deus.bot.domain.Audit;

@FunctionalInterface
public interface GetGuildMessageAuditListOutbound {
    List<Audit> getGuildMessageAuditList(String guildId);
}
