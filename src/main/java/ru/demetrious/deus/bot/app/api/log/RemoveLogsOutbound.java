package ru.demetrious.deus.bot.app.api.log;

import java.time.Instant;
import java.util.List;
import ru.demetrious.deus.bot.domain.Log;

@FunctionalInterface
public interface RemoveLogsOutbound {
    List<Log> removeLogs(Instant before);
}
