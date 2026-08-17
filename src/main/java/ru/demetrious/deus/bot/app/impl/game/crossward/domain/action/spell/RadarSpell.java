package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell;

import java.util.Map;
import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag;

import static java.util.Map.Entry;
import static java.util.Map.of;
import static java.util.Objects.nonNull;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.SCORE_COEFFICIENT_FUNCTION;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_HIGH;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_LOW;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_MEDIUM;

@Builder
public record RadarSpell(int x, int y, Character letter) implements Spell {
    private static final Map<Tag, Integer> TAG_RADIUS = of(
        FREQUENCY_HIGH, 1,
        FREQUENCY_MEDIUM, 2,
        FREQUENCY_LOW, 3
    );

    @Override
    public void use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        int radius = TAG_RADIUS.entrySet().stream()
            .filter(t -> gameSession.getLetterTags().get(t.getKey()).contains(letter))
            .findFirst()
            .map(Entry::getValue)
            .orElseThrow(() -> new ActionException("No such letter tag: " + letter));

        int score = player.getScore();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                Cell cell = gameSession.getGrid().get(new Position(i, j));

                if (nonNull(cell) && letter.equals(cell.getLetter())) {
                    score += cell.reveal(SCORE_COEFFICIENT_FUNCTION.apply(player));
                }
            }
        }
        player.setScore(score);
    }
}
