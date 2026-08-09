package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import lombok.Data;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.WAITING;

@Data
public class State {
    private Phase phase = WAITING;
    private boolean locked = false;
    private int currentPlayer = 0;

    public enum Phase {
        WAITING, PLAYING, FINISHED
    }
}
