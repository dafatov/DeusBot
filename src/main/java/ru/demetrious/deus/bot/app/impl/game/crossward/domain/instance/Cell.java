package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Cell {
    private final Position position;
    private final Character letter;
    private final Set<Tag> tags = new HashSet<>();
    private boolean revealed = false;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<Word> words = new ArrayList<>();

    public int reveal(Function<Word, Integer> wordCoefficientFunction) {
        return reveal(null, wordCoefficientFunction);
    }

    protected int reveal(Word excludeWord, Function<Word, Integer> wordCoefficientFunction) {
        if (revealed) {
            return 0;
        }

        int points = words.stream()
            .filter(word -> word != excludeWord)
            .mapToInt(word -> word.reveal(this, wordCoefficientFunction))
            .sum();

        revealed = true;
        return points;
    }
}
