package ru.demetrious.deus.bot.fw.config.quartz;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

import static java.util.Objects.nonNull;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@RequiredArgsConstructor
@Configuration
public class QuartzConfig {
    private final List<QuartzJobBean> quartzJobBeanList;
    private final Scheduler scheduler;

    @PostConstruct
    public void init() {
        quartzJobBeanList.stream()
            .filter(quartzJobBean -> nonNull(getAnnotation(quartzJobBean)))
            .map(quartzJobBean -> {
                JobDetail jobDetail = newJob(quartzJobBean.getClass())
                    .storeDurably()
                    .requestRecovery()
                    .build();

                return Pair.of(jobDetail, newTrigger()
                    .forJob(jobDetail)
                    .withSchedule(cronSchedule(getAnnotation(quartzJobBean).cron()))
                    .build());
            }).forEach(this::scheduleJob);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private InitScheduled getAnnotation(QuartzJobBean quartzJobBean) {
        return quartzJobBean.getClass().getAnnotation(InitScheduled.class);
    }

    private void scheduleJob(Pair<JobDetail, ? extends Trigger> schedulePair) {
        try {
            scheduler.scheduleJob(schedulePair.getLeft(), schedulePair.getRight());
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
