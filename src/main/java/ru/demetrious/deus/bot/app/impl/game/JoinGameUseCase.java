package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.JoinGameInbound;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;

@RequiredArgsConstructor
@Component
public class JoinGameUseCase implements JoinGameInbound {
    private final Gamebox gamebox;

    @Override
    public void execute(String gameId) {
        gamebox.joinGame(gameId);
    }
}
