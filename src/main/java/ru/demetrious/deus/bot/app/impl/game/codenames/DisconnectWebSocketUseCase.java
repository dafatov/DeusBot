package ru.demetrious.deus.bot.app.impl.game.codenames;

import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.DisconnectWebSocketInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisconnectWebSocketUseCase implements DisconnectWebSocketInbound {
    private final Gamebox gamebox;
    private final ExecutorService virtualThreadPerTaskExecutor;
    private final NotifyGameStateOutbound notifyGameStateOutbound;

    @Override
    public void execute(String userId) {
        gamebox.findByPlayer(userId).ifPresent(pair -> {
            pair.getRight().setDisconnectCompletableFuture(runAsync(() -> {
                pair.getLeft().getPlayerList().removeIf(p -> p.getId().equals(userId));

                log.debug("Disconnect for {}", userId);
                if (pair.getLeft().getPlayerList().isEmpty()) {
                    log.debug("Remove game cause no players: {}", pair.getLeft().getKey());
                    gamebox.removeGame(pair.getLeft().getKey());
                } else {
                    notifyGameStateOutbound.notifyGameState(pair.getLeft());
                }
            }, delayedExecutor(30, SECONDS, virtualThreadPerTaskExecutor)));
            notifyGameStateOutbound.notifyGameState(pair.getLeft());
        });
    }
}
