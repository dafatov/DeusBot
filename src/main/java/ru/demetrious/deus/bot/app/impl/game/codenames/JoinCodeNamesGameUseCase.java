package ru.demetrious.deus.bot.app.impl.game.codenames;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.JoinCodeNamesGameInbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;

@RequiredArgsConstructor
@Component
public class JoinCodeNamesGameUseCase implements JoinCodeNamesGameInbound {
    private final Gamebox gamebox;

    @Override
    public void execute(String gameId) {
        gamebox.joinGame(gameId);
    }
}
