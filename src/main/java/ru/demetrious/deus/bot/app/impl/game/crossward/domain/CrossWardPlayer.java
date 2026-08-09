package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.awt.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class CrossWardPlayer extends Player {
    private boolean isSpectator = true;
    private Color color;
    private int score;

    public CrossWardPlayer(String id, String name, String avatar) {
        super(id, name, avatar);
    }
}
