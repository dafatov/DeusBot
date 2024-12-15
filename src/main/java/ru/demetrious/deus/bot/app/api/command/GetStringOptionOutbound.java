package ru.demetrious.deus.bot.app.api.command;

import java.util.Optional;

@FunctionalInterface
public interface GetStringOptionOutbound {
    Optional<String> getStringOption(String name);
}
