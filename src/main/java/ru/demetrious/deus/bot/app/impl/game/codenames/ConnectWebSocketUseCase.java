package ru.demetrious.deus.bot.app.impl.game.codenames;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.ConnectWebSocketInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConnectWebSocketUseCase implements ConnectWebSocketInbound {
    private final Gamebox gamebox;
    private final NotifyGameStateOutbound notifyGameStateOutbound;

    @Override
    public void execute(String userId) {
        gamebox.findByPlayer(userId)
            .filter(pair -> nonNull(pair.getRight().getDisconnectCompletableFuture()))
            .ifPresent(pair -> {
                log.debug("Cancel Disconnect Timer for {}", userId);
                pair.getRight().getDisconnectCompletableFuture().cancel(true);
                pair.getRight().setDisconnectCompletableFuture(null);
                notifyGameStateOutbound.notifyGameState(pair.getLeft());
            });
    }
}
