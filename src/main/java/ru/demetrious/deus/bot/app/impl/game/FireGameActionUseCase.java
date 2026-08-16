package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.FireGameActionInbound;
import ru.demetrious.deus.bot.app.api.game.NotifyGameErrorOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;

import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@Slf4j
@RequiredArgsConstructor
@Component
public class FireGameActionUseCase implements FireGameActionInbound {
    private final Gamebox gamebox;
    private final NotifyGameErrorOutbound notifyGameErrorOutbound;

    @Override
    public void execute(String gameId, String userId, Action<?, ?, ?> action) {
        log.debug("execute: gameId={}, userId={}, action={}", gameId, userId, writeValueAsString(action));

        try {
            gamebox.performAction(gameId, userId, action);
        } catch (Exception e) {
            log.warn(e.toString());
            log.trace(e.getMessage(), e);
            notifyGameErrorOutbound.notifyGameError(userId, e);
        }
    }
}
