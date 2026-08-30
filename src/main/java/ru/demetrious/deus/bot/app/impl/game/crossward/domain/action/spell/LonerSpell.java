package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.SCORE_COEFFICIENT_FUNCTION;

@Builder
public record LonerSpell(int x, int y) implements Spell {
    private static final int RADIUS = 3;

    @Override
    public boolean use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx, ActionEvent<CrossWardAction, CrossWardPlayer> event) throws ActionException {
        List<Cell> cells = getCellsInRadius(gameSession);
        Map<Character, Long> letterFrequency = cells.stream()
            .collect(groupingBy(Cell::getLetter, counting()));
        List<Cell> targetCells = cells.stream()
            .filter(cell -> !cell.isRevealed())
            .peek(cell -> cell.getHistory().addFirst(event))
            .filter(cell -> letterFrequency.get(cell.getLetter()) == 1)
            .toList();

        if (targetCells.isEmpty()) return false;

        Cell cell = targetCells.get(current().nextInt(targetCells.size()));

        player.setScore(player.getScore() + cell.reveal(SCORE_COEFFICIENT_FUNCTION.apply(player)));
        return false;
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private List<Cell> getCellsInRadius(CrossWardInstance gameSession) {
        return rangeClosed(x - RADIUS, x + RADIUS)
            .boxed()
            .flatMap(i -> rangeClosed(y - RADIUS, y + RADIUS)
                .mapToObj(j -> gameSession.getGrid().get(new Position(i, j)))
                .filter(Objects::nonNull))
            .collect(toList());
    }
}
