package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import java.util.List;
import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static java.util.Collections.shuffle;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toList;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.BLUE;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.RED;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.SPECTATOR;

@Builder
public record ShufflePlayersAction() implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, String userId, CodeNamesActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, userId);

        List<CodeNamesPlayer> list = gameSession.getPlayerList().stream().filter(p -> !p.getTeam().equals(SPECTATOR)).collect(toList());

        shuffle(list, current());

        int red = list.size() / 2;
        int last = list.size() - 1;
        for (int i = 0; i < list.size(); i++) {
            CodeNamesPlayer player = list.get(i);
            boolean isRed = i < red;

            player.setTeam(isRed ? RED : BLUE);
            player.setCaptain((isRed && i == 0) || (!isRed && i == last));
        }
    }
}
