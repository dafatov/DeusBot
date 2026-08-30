package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Event;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static java.lang.Math.toIntExact;
import static java.util.Objects.nonNull;

@Accessors(chain = true)
@Data
public class Word {
    private final String text;
    private final Orientation orientation;
    private final Position start;
    private final List<Cell> cells = new ArrayList<>();
    private final List<Event> history = new ArrayList<>();
    private CrossWardPlayer owner;
    private int order;
    private boolean revealed = false;

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public int reveal(Function<Word, Integer> wordCoefficientFunction) {
        return reveal(null, wordCoefficientFunction);
    }

    protected int reveal(Cell excludeCell, Function<Word, Integer> wordCoefficientFunction) {
        if (revealed) {
            return 0;
        }

        if (nonNull(excludeCell) && cells.stream().filter(cell -> cell != excludeCell).anyMatch(cell -> !cell.isRevealed())) {
            return 0;
        }

        revealed = true;
        history.clear();

        int initial = toIntExact(cells.stream().filter(Cell::isRevealed).count());
        int points = cells.stream()
            .mapToInt(cell -> cell.reveal(this, wordCoefficientFunction))
            .sum();
        int after = toIntExact(cells.stream().filter(Cell::isRevealed).count());
        int newCells = after - initial;

        return points + newCells * wordCoefficientFunction.apply(this);
    }
}
