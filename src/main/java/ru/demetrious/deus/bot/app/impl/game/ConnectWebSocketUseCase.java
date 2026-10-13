package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.ConnectWebSocketInbound;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConnectWebSocketUseCase implements ConnectWebSocketInbound {
    private final Gamebox gamebox;

    @Override
    public void execute(String userId) {
        gamebox.connect(userId);
    }
}
