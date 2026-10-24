package ru.demetrious.deus.bot.app.impl.game.common.domain;

import lombok.Getter;

@Getter
public class ActionEvent<A extends Action<P, ?, ?, A>, P extends Player> extends Event {
    private final A action;
    private final P issuer;

    public ActionEvent(int id, A action, P issuer) {
        super(id);
        this.action = action;
        this.issuer = issuer;
    }
}
