package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation;

import static java.util.Map.entry;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.HORIZONTAL;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.VERTICAL;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrossWardInstance extends Instance<CrossWardSetting, CrossWardPlayer> {
    private final Map<Position, Cell> grid = new HashMap<>();
    private final List<Word> words = new ArrayList<>();

    public CrossWardInstance(String key, String hostId, CrossWardSetting setting) {
        super(key, hostId, setting);
        init();
    }

    //TODO удалить когда не будут нужны искусственные данные
    public void init() {
        Map<Pair<Position, Orientation>, String> words = Map.ofEntries(
            entry(Pair.of(new Position(0, 0), HORIZONTAL), "КОТ"),
            entry(Pair.of(new Position(4, 0), HORIZONTAL), "море"),
            entry(Pair.of(new Position(0, 2), HORIZONTAL), "рак"),
            entry(Pair.of(new Position(4, 2), HORIZONTAL), "река"),
            entry(Pair.of(new Position(0, 4), HORIZONTAL), "лес"),
            entry(Pair.of(new Position(4, 4), HORIZONTAL), "нос"),
            entry(Pair.of(new Position(0, 6), HORIZONTAL), "слонка"),

            entry(Pair.of(new Position(0, 0), VERTICAL), "кар"),
            entry(Pair.of(new Position(2, 0), VERTICAL), "так"),
            entry(Pair.of(new Position(0, 4), VERTICAL), "лос"),
            entry(Pair.of(new Position(2, 4), VERTICAL), "сто"),
            entry(Pair.of(new Position(4, 0), VERTICAL), "моронык")
        );

        words.forEach((pair, text) -> {
            Orientation orientation = pair.getRight();
            Position start = pair.getLeft();
            Word word = new Word(text, orientation, start);
            List<Cell> wordCells = new ArrayList<>();   // только ячейки этого слова

            char[] letters = word.getText().toCharArray();
            for (int i = 0; i < letters.length; i++) {
                char letter = letters[i];
                int dx = orientation == HORIZONTAL ? i : 0;
                int dy = orientation == VERTICAL ? i : 0;
                Position pos = new Position(start.x() + dx, start.y() + dy);

                Cell cell = grid.compute(pos, (p, existing) -> {
                    if (existing != null && !equalsIgnoreCase(existing.getLetter().toString(), String.valueOf(letter))) {
                        throw new IllegalStateException("Некорректное пересечение слов");
                    }
                    if (existing == null) {
                        existing = new Cell(p, letter);
                    }
                    existing.getWords().add(word);
                    return existing;
                });
                wordCells.add(cell);
            }

            word.getCells().addAll(wordCells);
            this.words.add(word);
        });
    }

    @Override
    public boolean isFinished() {
        //TODO реализовать при реализации игровых фаз
        return false;
    }
}
