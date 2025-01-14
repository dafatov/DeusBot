package ru.demetrious.deus.bot.fw.config.quartz;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
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
    private final List<? extends QuartzJobBean> quartzJobBeanList;
    private final Scheduler scheduler;

    @PostConstruct
    public void init() {
        quartzJobBeanList.stream()
            .filter(quartzJobBean -> nonNull(getAnnotation(quartzJobBean)))
            .map(quartzJobBean -> {
                InitScheduled initScheduled = getAnnotation(quartzJobBean);
                JobDetail jobDetail = newJob(quartzJobBean.getClass())
                    .withIdentity(initScheduled.name(), initScheduled.groupName())
                    .requestRecovery()
                    .build();

                return Pair.of(jobDetail, newTrigger()
                    .withIdentity(initScheduled.name(), initScheduled.groupName())
                    .forJob(jobDetail)
                    .withSchedule(cronSchedule(initScheduled.cron()))
                    .build());
            }).forEach(this::scheduleJob);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private InitScheduled getAnnotation(QuartzJobBean quartzJobBean) {
        return quartzJobBean.getClass().getAnnotation(InitScheduled.class);
    }

    @SneakyThrows
    private void scheduleJob(Pair<JobDetail, ? extends Trigger> schedulePair) {
        if (scheduler.checkExists(schedulePair.getRight().getKey())) {
            return;
        }

        scheduler.scheduleJob(schedulePair.getLeft(), schedulePair.getRight());
    }
}
