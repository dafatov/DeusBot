package ru.demetrious.deus.bot.app.api.log;

import java.util.List;
import ru.demetrious.deus.bot.domain.Log;

@FunctionalInterface
public interface GetLogListOutbound {
    List<Log> getLogList();
}
