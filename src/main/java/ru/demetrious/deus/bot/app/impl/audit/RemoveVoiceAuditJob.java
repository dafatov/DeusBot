package ru.demetrious.deus.bot.app.impl.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.voice.RemoveVoiceAuditOutbound;

@Slf4j
@RequiredArgsConstructor
@Component
public class RemoveVoiceAuditJob extends AuditJob {
    private final RemoveVoiceAuditOutbound removeVoiceAuditOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        removeVoiceAuditOutbound.removeVoiceAudit();
        log.info("Voice audits were removed");
    }
}
