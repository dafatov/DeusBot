package ru.demetrious.deus.bot.adapter.duplex.ui.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.ErrorMapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.GameSessionMapper;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameErrorOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;

@Slf4j
@RequiredArgsConstructor
@Component
public class UiAdapter implements NotifyGameStateOutbound, NotifyGameErrorOutbound {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionMapper gameSessionMapper;
    private final ErrorMapper errorMapper;

    @Override
    public void notifyGameState(GameSession gameSession) {
        gameSession.getPlayerList().forEach(player -> simpMessagingTemplate.convertAndSendToUser(
            player.getId(),
            "/game/%s".formatted(gameSession.getKey()),
            gameSessionMapper.map(gameSession, player, gameSession.getState().getPhase())
        ));
    }

    @Override
    public void sendGameError(String userId, Exception e) {
        simpMessagingTemplate.convertAndSendToUser(userId, "/error", errorMapper.map(e));
    }
}
