package ru.demetrious.deus.bot.app.impl.audit;

import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

import static ru.demetrious.deus.bot.app.impl.audit.AuditJob.CRON;

@InitScheduled(cron = CRON)
public abstract class AuditJob extends QuartzJobBean {
    protected static final String CRON = "0 0 6 ? * 5";
}
