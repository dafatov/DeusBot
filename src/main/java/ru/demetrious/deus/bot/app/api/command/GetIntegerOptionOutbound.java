package ru.demetrious.deus.bot.app.api.command;

import java.util.Optional;

@FunctionalInterface
public interface GetIntegerOptionOutbound {
    Optional<Integer> getIntegerOption(String name);
}
