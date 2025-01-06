package ru.demetrious.deus.bot.app.impl.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.message.RemoveMessageAuditOutbound;

@Slf4j
@RequiredArgsConstructor
@Component
public class RemoveMessageAuditJob extends AuditJob {
    private final RemoveMessageAuditOutbound removeMessageAuditOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        removeMessageAuditOutbound.removeMessageAudit();
        log.info("Message audits were removed");
    }
}
