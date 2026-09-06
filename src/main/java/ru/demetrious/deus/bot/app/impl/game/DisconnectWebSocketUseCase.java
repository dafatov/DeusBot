package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.DisconnectWebSocketInbound;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisconnectWebSocketUseCase implements DisconnectWebSocketInbound {
    private final Gamebox gamebox;

    @Override
    public void execute(String userId) {
        gamebox.disconnect(userId);
    }
}
