package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

@Accessors(chain = true)
@Data
public class Word {
    private final String text;
    private final Orientation orientation;
    private final Position start;
    private final List<Cell> cells = new ArrayList<>();
    private CrossWardPlayer owner;

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}
