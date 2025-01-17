package ru.demetrious.deus.bot.app.api.freebie;

import java.util.List;
import ru.demetrious.deus.bot.domain.freebie.FreebieItem;

@FunctionalInterface
public interface GetFreebieListOutbound {
    List<FreebieItem> getFreebieList();
}
