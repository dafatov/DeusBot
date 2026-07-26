package ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance;

import com.google.common.collect.Multiset;
import lombok.Data;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;

import static com.google.common.collect.EnumMultiset.create;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.WAITING;

@Data
public class State {
    private final Multiset<Team> score = create(Team.class);
    private int round = 0;
    private Phase phase = WAITING;
    private Team team;
    private boolean locked = false;

    public enum Phase {
        WAITING, HINTING, GUESSING, FINISHED
    }
}
