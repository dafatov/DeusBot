package ru.demetrious.deus.bot.app.impl.log;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.log.RemoveLogsOutbound;
import ru.demetrious.deus.bot.domain.Log;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@InitScheduled(name = "remove-before", groupName = "log", cron = "0 0 0/1 ? * *")
@Slf4j
@RequiredArgsConstructor
@Component
public class RemoveLogJob extends QuartzJobBean {
    private final RemoveLogsOutbound removeLogsOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        List<Log> removed = removeLogsOutbound.removeLogs(now().minus(7, DAYS));

        if (isNotEmpty(removed)) {
            log.info("{} logs were removed", removed.size());
        }
    }
}
