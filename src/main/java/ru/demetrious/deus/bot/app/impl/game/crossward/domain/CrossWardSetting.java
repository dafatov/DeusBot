package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

public record CrossWardSetting(Long packId) implements Setting {
    @Override
    public String getGame() {
        return CROSS_WARD;
    }
}
