package ru.demetrious.deus.bot.app.impl.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.message.RemoveMessageAuditOutbound;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

@InitScheduled(name = "remove-message", groupName = "audit", cron = "0 0 6 ? * 5")
@Slf4j
@RequiredArgsConstructor
@Component
public class RemoveMessageAuditJob extends QuartzJobBean {
    private final RemoveMessageAuditOutbound removeMessageAuditOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        removeMessageAuditOutbound.removeMessageAudit();
        log.info("Message audits were removed");
    }
}
