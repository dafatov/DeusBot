package ru.demetrious.deus.bot.adapter.duplex.ui.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.ErrorMapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.InstanceMapper;
import ru.demetrious.deus.bot.app.api.game.NotifyGameErrorOutbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;

@Slf4j
@RequiredArgsConstructor
@Component
public class UiAdapter implements NotifyGameStateOutbound, NotifyGameErrorOutbound {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final InstanceMapper instanceMapper;
    private final ErrorMapper errorMapper;

    @Override
    public void notifyGameState(Instance<?, ?, ?> gameSession) {
        gameSession.getPlayerList().forEach(player -> simpMessagingTemplate.convertAndSendToUser(
            player.getId(),
            "/game/%s".formatted(gameSession.getKey()),
            instanceMapper.map(gameSession, player, gameSession.isFinished())
        ));
    }

    @Override
    public void notifyGameError(String userId, Exception e) {
        simpMessagingTemplate.convertAndSendToUser(userId, "/error", errorMapper.map(e));
    }
}
