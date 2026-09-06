package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.util.function.Function;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

public record CrossWardSetting(Long packId) implements Setting {
    public static final int TARGET_SCORE = 100;
    public static final Function<CrossWardPlayer, Function<Word, Integer>> SCORE_COEFFICIENT_FUNCTION = p -> w -> p.equals(w.getOwner()) ? 2 : 1;

    @Override
    public String getGame() {
        return CROSS_WARD;
    }
}
