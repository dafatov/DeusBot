package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Cell {
    private final Position position;
    private final Character letter;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<?> words = new ArrayList<>();
}
