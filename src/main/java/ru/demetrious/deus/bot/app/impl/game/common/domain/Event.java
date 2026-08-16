package ru.demetrious.deus.bot.app.impl.game.common.domain;

public record Event<A extends Action<?, ?, ?>, P extends Player>(A action, P issuer) {
}
