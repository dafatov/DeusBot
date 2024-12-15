package ru.demetrious.deus.bot.app.api.event;

@FunctionalInterface
public interface HasEventOutbound {
    boolean hasEvent();
}
