package ru.demetrious.deus.bot.app.api.player;

@FunctionalInterface
public interface LeaveIfAloneInbound {
    void execute(String guildId, boolean isNeedLeave);
}
