package ru.demetrious.deus.bot.adapter.output.repository.audit;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.audit.IncrementAuditOutbound;
import ru.demetrious.deus.bot.app.api.command.GetGuildCommandAuditListOutbound;
import ru.demetrious.deus.bot.app.api.message.GetGuildMessageAuditListOutbound;
import ru.demetrious.deus.bot.app.api.message.RemoveMessageAuditOutbound;
import ru.demetrious.deus.bot.app.api.voice.GetGuildVoiceAuditListOutbound;
import ru.demetrious.deus.bot.app.api.voice.RemoveVoiceAuditOutbound;
import ru.demetrious.deus.bot.domain.Audit;

import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.domain.Audit.Type.COMMAND;
import static ru.demetrious.deus.bot.domain.Audit.Type.MESSAGE;
import static ru.demetrious.deus.bot.domain.Audit.Type.VOICE;

@Transactional
@RequiredArgsConstructor
@Component
public class AuditAdapter implements IncrementAuditOutbound, GetGuildVoiceAuditListOutbound, GetGuildMessageAuditListOutbound, RemoveMessageAuditOutbound,
    RemoveVoiceAuditOutbound, GetGuildCommandAuditListOutbound {
    private final AuditRepository auditRepository;

    @Override
    public void incrementAudit(Audit.AuditId auditId) {
        incrementAudit(auditId, 1L);
    }

    @Override
    public void incrementAudit(Audit.AuditId auditId, Long count) {
        Audit audit = auditRepository.findById(auditId)
            .orElseGet(() -> new Audit().setAuditId(auditId));

        audit.setCount(ofNullable(audit.getCount()).orElse(0L) + count);

        auditRepository.save(audit);
    }

    @Override
    public List<Audit> getGuildVoiceAuditList(String guildId) {
        return auditRepository.findByAuditId_GuildIdAndAuditId_Type(guildId, VOICE);
    }

    @Override
    public List<Audit> getGuildMessageAuditList(String guildId) {
        return auditRepository.findByAuditId_GuildIdAndAuditId_Type(guildId, MESSAGE);
    }

    @Override
    public void removeVoiceAudit() {
        auditRepository.deleteAllByAuditId_Type(VOICE);
    }

    @Override
    public void removeMessageAudit() {
        auditRepository.deleteAllByAuditId_Type(MESSAGE);
    }

    @Override
    public List<Audit> getGuildCommandAuditList(String guildId) {
        return auditRepository.findByAuditId_GuildIdAndAuditId_Type(guildId, COMMAND);
    }
}
