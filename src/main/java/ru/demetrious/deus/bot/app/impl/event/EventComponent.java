package ru.demetrious.deus.bot.app.impl.event;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.utils.Key;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.impl.publication.EventPublicationJob;

import static java.lang.Math.floorDiv;
import static java.util.Optional.ofNullable;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

@RequiredArgsConstructor
@Component
public class EventComponent {
    public static final String GUILD_ID = "guildId";
    public static final String USER_ID = "userId";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    private static final String EVENT = "event";

    private final Scheduler scheduler;

    @SneakyThrows
    public Optional<Long> createEvent(Optional<String> userId, String description, String title, String guildId, String cronExpression) {
        Trigger trigger = saveTrigger(cronExpression, title, createJob(title), createJobDataMap(userId, description, title, guildId));

        return ofNullable(trigger.getNextFireTime())
            .map(Date::toInstant)
            .map(Instant::toEpochMilli)
            .map(milli -> floorDiv(milli, 1000));
    }

    @SneakyThrows
    public List<String> getEventKeyList() {
        return scheduler.getJobKeys(groupEquals(EVENT)).stream()
            .map(Key::getName)
            .toList();
    }

    @SneakyThrows
    public List<Trigger> getTriggerList() {
        return scheduler.getTriggerKeys(groupEquals(EVENT)).stream()
            .map(this::getTrigger)
            .toList();
    }

    @SneakyThrows
    public boolean removeEvent(String title) {
        return scheduler.deleteJob(new JobKey(title, EVENT));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private JobDetail createJob(String name) {
        return newJob(EventPublicationJob.class)
            .withIdentity(name, EventComponent.EVENT)
            .requestRecovery()
            .build();
    }

    private Trigger saveTrigger(String cronExpression, String name, JobDetail jobDetail, Map<String, ?> jobDataMap) throws SchedulerException {
        Trigger trigger = newTrigger()
            .withIdentity(name, EventComponent.EVENT)
            .withSchedule(cronSchedule(cronExpression))
            .usingJobData(new JobDataMap(jobDataMap))
            .build();

        if (scheduler.checkExists(trigger.getKey())) {
            scheduler.rescheduleJob(trigger.getKey(), trigger);
        } else {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        return trigger;
    }

    private Map<String, String> createJobDataMap(Optional<String> userIdOptional, String description, String title, String guildId) {
        Map<String, String> objectObjectHashMap = new HashMap<>();

        objectObjectHashMap.put(TITLE, title);
        objectObjectHashMap.put(DESCRIPTION, description);
        userIdOptional.ifPresent(userId -> objectObjectHashMap.put(USER_ID, userId));
        objectObjectHashMap.put(GUILD_ID, guildId);
        return objectObjectHashMap;
    }

    @SneakyThrows
    private Trigger getTrigger(TriggerKey triggerKey) {
        return scheduler.getTrigger(triggerKey);
    }
}
