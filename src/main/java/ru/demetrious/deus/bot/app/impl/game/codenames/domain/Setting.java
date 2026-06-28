package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public record Setting(Long packId) {
}
