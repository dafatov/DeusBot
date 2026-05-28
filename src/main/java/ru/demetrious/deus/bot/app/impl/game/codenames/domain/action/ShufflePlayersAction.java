package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import java.util.List;
import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player;

import static java.util.Collections.shuffle;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toList;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team.BLUE;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team.RED;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team.SPECTATOR;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkLocked;

@Builder
public record ShufflePlayersAction() implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, userId);

        List<Player> list = gameSession.getPlayerList().stream().filter(p -> !p.getTeam().equals(SPECTATOR)).collect(toList());

        shuffle(list, current());

        int red = list.size() / 2;
        int last = list.size() - 1;
        for (int i = 0; i < list.size(); i++) {
            Player player = list.get(i);
            boolean isRed = i < red;

            player.setTeam(isRed ? RED : BLUE);
            player.setCaptain((isRed && i == 0) || (!isRed && i == last));
        }
    }
}
