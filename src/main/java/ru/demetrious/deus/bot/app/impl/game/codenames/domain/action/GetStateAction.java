package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;

@Builder
public record GetStateAction() implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) {
    }
}
