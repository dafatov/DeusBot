package ru.demetrious.deus.bot.app.api.voice;

import java.util.List;
import ru.demetrious.deus.bot.domain.Audit;

@FunctionalInterface
public interface GetGuildVoiceAuditListOutbound {
    List<Audit> getGuildVoiceAuditList(String guildId);
}
