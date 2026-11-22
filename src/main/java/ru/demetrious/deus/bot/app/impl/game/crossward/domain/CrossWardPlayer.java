package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.awt.Color;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

@Accessors(chain = true)
@Data
public class CrossWardPlayer extends Player {
    private final Color color;
    private boolean isSpectator = true;
    private int score;

    public CrossWardPlayer(String id, String name, String avatar, Color color) {
        super(id, name, avatar);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
