package ru.demetrious.deus.bot.app.api.pull;

import ru.demetrious.deus.bot.domain.PullsData;

public interface UpdatePullsDataOutbound {
    void updatePullsData(PullsData pullsData);
}
