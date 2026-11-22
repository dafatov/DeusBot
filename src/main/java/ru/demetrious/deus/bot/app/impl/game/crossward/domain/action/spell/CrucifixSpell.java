package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.SCORE_COEFFICIENT_FUNCTION;

@Slf4j
@Builder
public record CrucifixSpell(int x, int y) implements Spell {
    private static final int MAX_STEP = 2;
    private static final int REQUIRED_CELLS_PER_STEP = 4;
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    @Override
    public boolean use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx, ActionEvent<CrossWardAction, CrossWardPlayer> event) throws ActionException {
        Position startPos = new Position(x, y);
        Cell startCell = gameSession.getGrid().get(startPos);

        if (isNull(startCell) || startCell.isRevealed()) {
            return false;
        }

        Function<Word, Integer> scoreCoefficient = SCORE_COEFFICIENT_FUNCTION.apply(player);
        int totalScore = player.getScore();

        totalScore += startCell.reveal(scoreCoefficient);

        for (int step = 1; step <= MAX_STEP; step++) {
            List<Cell> cellsToReveal = collectCellsAtDistance(gameSession.getGrid(), step);

            if (cellsToReveal.size() < REQUIRED_CELLS_PER_STEP) {
                break;
            }

            totalScore += cellsToReveal.stream().mapToInt(cell -> cell.reveal(scoreCoefficient)).sum();
        }

        player.setScore(totalScore);
        return true;
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private List<Cell> collectCellsAtDistance(Map<Position, Cell> grid, int step) {
        return stream(DIRECTIONS)
            .map(direction -> new Position(x + direction[0] * step, y + direction[1] * step))
            .map(grid::get)
            .filter(cell -> !isNull(cell) && !cell.isRevealed())
            .toList();
    }
}
