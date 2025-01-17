package ru.demetrious.deus.bot.app.api.log;

@FunctionalInterface
public interface GetLogFileInbound {
    byte[] getLogFile();
}
