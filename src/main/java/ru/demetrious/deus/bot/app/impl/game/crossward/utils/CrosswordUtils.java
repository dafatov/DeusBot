package ru.demetrious.deus.bot.app.impl.game.crossward.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation;

import static java.util.Collections.shuffle;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.HORIZONTAL;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.VERTICAL;

@Slf4j
@UtilityClass
public class CrosswordUtils {
    public static Optional<Word> tryPlaceWord(String text, List<Word> words, Map<Position, Cell> grid) {
        if (words.isEmpty()) {
            return of(placeWord(text, new PlacementVariant(new Position(0, 0), current().nextBoolean() ? HORIZONTAL : VERTICAL), words, grid));
        }

        List<PlacementVariant> variants = createVariants(text, grid);

        shuffle(variants, current());

        for (PlacementVariant variant : variants) {
            if (canPlaceWord(text, variant, grid)) {
                return of(placeWord(text, variant, words, grid));
            }
        }
        return empty();
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static List<PlacementVariant> createVariants(String text, Map<Position, Cell> grid) {
        List<PlacementVariant> variants = new ArrayList<>();

        for (Position pos : grid.keySet()) {
            Character letter = grid.get(pos).getLetter();

            for (int i = 0; i < text.length(); i++) {
                if (letter.equals(text.charAt(i))) {
                    variants.add(new PlacementVariant(new Position(pos.x() - i, pos.y()), HORIZONTAL));
                    variants.add(new PlacementVariant(new Position(pos.x(), pos.y() - i), VERTICAL));
                }
            }
        }
        return variants;
    }

    private static boolean canPlaceWord(String text, PlacementVariant variant, Map<Position, Cell> grid) {
        Set<Position> newPositions = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            int dx = variant.orientation == HORIZONTAL ? i : 0;
            int dy = variant.orientation == VERTICAL ? i : 0;
            newPositions.add(new Position(variant.start.x() + dx, variant.start.y() + dy));
        }

        for (int i = 0; i < text.length(); i++) {
            int dx = variant.orientation == HORIZONTAL ? i : 0;
            int dy = variant.orientation == VERTICAL ? i : 0;
            Position pos = new Position(variant.start.x() + dx, variant.start.y() + dy);
            Cell existing = grid.get(pos);
            char letter = text.charAt(i);

            if (existing != null) {
                if (existing.getLetter() != letter) {
                    return false;
                }

                for (Word word : existing.getWords()) {
                    if (word.getOrientation() == variant.orientation) {
                        return false;
                    }
                }
            } else {
                Position[] neighbors = {
                    new Position(pos.x() - 1, pos.y()),
                    new Position(pos.x() + 1, pos.y()),
                    new Position(pos.x(), pos.y() - 1),
                    new Position(pos.x(), pos.y() + 1)
                };

                for (Position neighbor : neighbors) {
                    if (grid.containsKey(neighbor) && !newPositions.contains(neighbor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static Word placeWord(String text, PlacementVariant variant, List<Word> words, Map<Position, Cell> grid) {
        Position position = variant.start();
        Orientation orientation = variant.orientation();
        Word word = new Word(words.size() + 1, text, orientation, position);
        List<Cell> wordCells = new ArrayList<>();

        char[] letters = word.getText().toCharArray();
        for (int i = 0; i < letters.length; i++) {
            char letter = letters[i];
            int dx = orientation == HORIZONTAL ? i : 0;
            int dy = orientation == VERTICAL ? i : 0;
            Position pos = new Position(position.x() + dx, position.y() + dy);

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
        words.add(word);
        return word;
    }

    private record PlacementVariant(Position start, Orientation orientation) {
    }
}
