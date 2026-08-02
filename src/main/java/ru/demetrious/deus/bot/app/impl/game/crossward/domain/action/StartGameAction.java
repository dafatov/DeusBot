package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation;

import static java.util.Collections.shuffle;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.HORIZONTAL;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word.Orientation.VERTICAL;

@Slf4j
@Builder
public record StartGameAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) {
        List<String> words = new ArrayList<>(ctx.getDictionary().getWords(gameSession.getSetting().packId()));

        shuffle(words, current());
        gameSession.getAvailableWords().clear();
        gameSession.getAvailableWords().addAll(words);

        placeWord(gameSession, 3);
    }

    private void placeWord(CrossWardInstance gameSession, int count) {
        int index = 0;
        while (index < count) {
            String text = gameSession.getAvailableWords().poll();
            log.info("[Added] {}", text);
            if (!tryPlaceWord(text, gameSession)) {
                gameSession.getAvailableWords().add(text);
            } else {
                index++;
            }
        }
    }

    private boolean tryPlaceWord(String text, CrossWardInstance gameSession) {
        if (gameSession.getWords().isEmpty()) {
            placeWord(text, new Position(0, 0), current().nextBoolean() ? HORIZONTAL : VERTICAL, gameSession);
            return true;
        }

        List<PlacementVariant> variants = createVariants(text, gameSession);

        shuffle(variants, current());

        for (PlacementVariant variant : variants) {
            if (canPlaceWord(text, variant, gameSession)) {
                placeWord(text, variant, gameSession);
                return true;
            }
        }
        return false;
    }

    private List<PlacementVariant> createVariants(String text, CrossWardInstance gameSession) {
        List<PlacementVariant> variants = new ArrayList<>();
        Map<Position, Cell> grid = gameSession.getGrid();

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

    private boolean canPlaceWord(String text, PlacementVariant variant, CrossWardInstance gameSession) {
        // Собираем все позиции нового слова
        Set<Position> newPositions = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            int dx = variant.orientation == Orientation.HORIZONTAL ? i : 0;
            int dy = variant.orientation == Orientation.VERTICAL ? i : 0;
            newPositions.add(new Position(variant.start.x() + dx, variant.start.y() + dy));
        }

        for (int i = 0; i < text.length(); i++) {
            int dx = variant.orientation == Orientation.HORIZONTAL ? i : 0;
            int dy = variant.orientation == Orientation.VERTICAL ? i : 0;
            Position pos = new Position(variant.start.x() + dx, variant.start.y() + dy);
            Cell existing = gameSession.getGrid().get(pos);
            char letter = text.charAt(i);

            if (existing != null) {
                // Проверка совпадения букв
                if (existing.getLetter() != letter) {
                    return false;
                }
                // Запрещаем пересечение с параллельными словами
                for (Word word : existing.getWords()) {
                    if (word.getOrientation() == variant.orientation) {
                        return false;
                    }
                }
            } else {
                // Проверка соседей (без диагоналей, т.к. проблема была в параллельности)
                Position[] neighbors = {
                    new Position(pos.x() - 1, pos.y()),
                    new Position(pos.x() + 1, pos.y()),
                    new Position(pos.x(), pos.y() - 1),
                    new Position(pos.x(), pos.y() + 1)
                };
                for (Position neighbor : neighbors) {
                    if (gameSession.getGrid().containsKey(neighbor) && !newPositions.contains(neighbor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeWord(String text, PlacementVariant variant, CrossWardInstance gameSession) {
        placeWord(text, variant.start(), variant.orientation(), gameSession);
    }

    private void placeWord(String text, Position position, Orientation orientation, CrossWardInstance gameSession) {
        List<Word> words = gameSession.getWords();
        Map<Position, Cell> grid = gameSession.getGrid();
        Word word = new Word(text, orientation, position);
        List<Cell> wordCells = new ArrayList<>();   // только ячейки этого слова

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
    }

    private record PlacementVariant(Position start, Orientation orientation) {
    }
}
