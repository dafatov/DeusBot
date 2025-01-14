package ru.demetrious.deus.bot.app.api.publicist;

import java.util.Optional;

@FunctionalInterface
public interface GetGuildPublicistOutbound {
    Optional<String> getGuildPublicist(String guildId);
}
