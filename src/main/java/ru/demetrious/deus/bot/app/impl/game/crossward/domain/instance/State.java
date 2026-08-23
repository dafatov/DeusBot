package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import java.time.Instant;
import lombok.Data;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.WAITING;

@Data
public class State {
    private Instant startedAt;
    private Phase phase = WAITING;
    private boolean locked = false;
    private CrossWardPlayer currentPlayer;
    private int currentEnergy;

    public enum Phase {
        WAITING, PLAYING, FINISHED
    }
}
