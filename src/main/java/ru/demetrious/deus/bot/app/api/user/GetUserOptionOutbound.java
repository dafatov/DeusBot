package ru.demetrious.deus.bot.app.api.user;

import java.util.Optional;

@FunctionalInterface
public interface GetUserOptionOutbound {
    Optional<String> getUserIdOption(String name);
}
