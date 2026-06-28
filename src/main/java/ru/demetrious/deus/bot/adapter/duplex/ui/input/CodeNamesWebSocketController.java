package ru.demetrious.deus.bot.adapter.duplex.ui.input;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.ActionMapper;
import ru.demetrious.deus.bot.app.api.game.codenames.ConnectWebSocketInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.DisconnectWebSocketInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.FireGameActionInbound;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CodeNamesWebSocketController {
    private final FireGameActionInbound fireGameActionInbound;
    private final DisconnectWebSocketInbound disconnectWebSocketInbound;
    private final ConnectWebSocketInbound connectWebSocketInbound;
    private final ActionMapper actionMapper;

    @MessageMapping("game/{gameId}")
    public void onAction(@DestinationVariable String gameId,
                         Principal principal,
                         @Payload ActionDto actionDto) {
        fireGameActionInbound.execute(gameId, principal.getName(), actionMapper.map(actionDto));
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        connectWebSocketInbound.execute(ofNullable(event.getUser())
            .map(Principal::getName)
            .orElseThrow());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        disconnectWebSocketInbound.execute(ofNullable(event.getUser())
            .map(Principal::getName)
            .orElseThrow());
    }
}
