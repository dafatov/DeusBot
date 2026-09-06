package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
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
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx, ActionEvent<CrossWardAction, CrossWardPlayer> event) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, player);

        shuffle(gameSession.getActivePlayers(), current());
    }
}
