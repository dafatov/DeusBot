package ru.demetrious.deus.bot.app.api.pull;

import java.util.Optional;
import ru.demetrious.deus.bot.domain.PullsData;

public interface FindPullsDataOutbound {
    Optional<PullsData> findPullsData();
}
