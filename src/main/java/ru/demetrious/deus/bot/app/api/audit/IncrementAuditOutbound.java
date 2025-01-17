package ru.demetrious.deus.bot.app.api.audit;

import ru.demetrious.deus.bot.domain.Audit;

public interface IncrementAuditOutbound {
    void incrementAudit(Audit.AuditId auditId);

    void incrementAudit(Audit.AuditId auditId, Long count);
}
