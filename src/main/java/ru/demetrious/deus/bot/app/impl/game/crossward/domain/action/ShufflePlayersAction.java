package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static java.util.Collections.shuffle;
import static java.util.concurrent.ThreadLocalRandom.current;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;


@Builder
public record ShufflePlayersAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, player);

        List<CrossWardPlayer> players = gameSession.getPlayerList();
        List<CrossWardPlayer> shuffled = new ArrayList<>(players);

        shuffle(shuffled, current());
        players.clear();
        players.addAll(shuffled);
    }
}
