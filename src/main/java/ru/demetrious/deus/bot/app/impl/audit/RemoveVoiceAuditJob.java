package ru.demetrious.deus.bot.app.impl.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.voice.RemoveVoiceAuditOutbound;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

@InitScheduled(name = "remove-voice", groupName = "audit", cron = "0 0 6 ? * 5")
@Slf4j
@RequiredArgsConstructor
@Component
public class RemoveVoiceAuditJob extends QuartzJobBean {
    private final RemoveVoiceAuditOutbound removeVoiceAuditOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        removeVoiceAuditOutbound.removeVoiceAudit();
        log.info("Voice audits were removed");
    }
}
