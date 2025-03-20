package ru.demetrious.deus.bot.app.api.log;

import java.time.Instant;

@FunctionalInterface
public interface GetLogFileInbound {
    byte[] getLogFile(Instant after);
}
