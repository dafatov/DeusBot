package ru.demetrious.deus.bot.app.api.publicist;

import java.util.List;
import ru.demetrious.deus.bot.domain.Publicist;

@FunctionalInterface
public interface GetPublicistListOutbound {
    List<Publicist> getPublisistList();
}
