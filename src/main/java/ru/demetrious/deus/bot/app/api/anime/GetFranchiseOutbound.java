package ru.demetrious.deus.bot.app.api.anime;

import java.util.List;
import ru.demetrious.deus.bot.domain.Franchise;

@FunctionalInterface
public interface GetFranchiseOutbound {
    List<Franchise> getFranchiseList();
}
