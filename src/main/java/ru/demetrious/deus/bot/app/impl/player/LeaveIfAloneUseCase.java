package ru.demetrious.deus.bot.app.impl.player;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.player.LeaveIfAloneInbound;
import ru.demetrious.deus.bot.app.impl.player.api.Jukebox;
import ru.demetrious.deus.bot.app.impl.player.api.Player;

import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static java.util.Date.from;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RequiredArgsConstructor
@Slf4j
@Component
public class LeaveIfAloneUseCase implements LeaveIfAloneInbound {
    private static final Duration DELAY = ofMinutes(5);
    private static final String TRIGGER_GROUP = "leave";

    private final Scheduler scheduler;
    private final Jukebox jukebox;

    @SneakyThrows
    @Override
    public void execute(String guildId, boolean isNeedLeave) {
        final Player player = jukebox.getPlayer(guildId);
        TriggerKey triggerKey = new TriggerKey(guildId, TRIGGER_GROUP);
        boolean isLeaveJobScheduled = scheduler.checkExists(triggerKey);

        log.debug("guildId={}, isNeedLeave={}, player.isPaused={}, isLeaveJobScheduled={}", guildId, isNeedLeave, player.isPaused(), isLeaveJobScheduled);
        if (isNeedLeave && !player.isPaused() || !isNeedLeave && player.isPaused()) {
            log.info(player.pause(true).getData()
                ? "Успешно установлена пауза, так как один"
                : "Успешно выключена пауза, так как не один");
        }

        if (!isNeedLeave && isLeaveJobScheduled) {
            scheduler.unscheduleJob(triggerKey);
            log.info("Успешно удалена задача на выход, так как не один");
            return;
        }

        if (isNeedLeave && !isLeaveJobScheduled) {
            saveTrigger(triggerKey);
            log.info("Успешно создана задача на выход, так как один");
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void saveTrigger(TriggerKey triggerKey) throws SchedulerException {
        JobDetail jobDetail = newJob(LeaveGuildPlayerJob.class)
            .withIdentity(triggerKey.getName(), triggerKey.getGroup())
            .requestRecovery()
            .build();
        Trigger trigger = newTrigger()
            .withIdentity(triggerKey)
            .startAt(from(now().plus(DELAY)))
            .withSchedule(simpleSchedule())
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
