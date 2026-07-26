package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

public record CodeNamesSetting(Long packId) implements Setting {
    @Override
    public String getGame() {
        return CODE_NAMES;
    }
}
