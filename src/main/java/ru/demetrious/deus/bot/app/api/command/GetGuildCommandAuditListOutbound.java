package ru.demetrious.deus.bot.app.api.command;

import java.util.List;
import ru.demetrious.deus.bot.domain.Audit;

@FunctionalInterface
public interface GetGuildCommandAuditListOutbound {
    List<Audit> getGuildCommandAuditList(String guildId);
}
