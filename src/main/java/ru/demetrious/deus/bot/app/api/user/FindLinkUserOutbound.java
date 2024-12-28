package ru.demetrious.deus.bot.app.api.user;

import java.util.Optional;
import ru.demetrious.deus.bot.domain.LinkUser;

@FunctionalInterface
public interface FindLinkUserOutbound {
    Optional<LinkUser> findById(LinkUser.LinkUserKey linkUserKey);
}
