package ru.demetrious.deus.bot.app.api.channel;

import java.util.Optional;

@FunctionalInterface
public interface GetChannelOptionOutbound {
    Optional<String> getChannelOption(String name);
}
