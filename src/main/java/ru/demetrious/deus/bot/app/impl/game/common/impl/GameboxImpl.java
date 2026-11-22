package ru.demetrious.deus.bot.app.impl.game.common.impl;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.impl.game.common.Processor;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class GameboxImpl implements Gamebox {
    private final Map<String, Processor<?, ?, ?, ?, ?>> processors;

    public GameboxImpl(List<Processor<?, ?, ?, ?, ?>> processors) {
        this.processors = processors.stream().collect(toMap(Processor::getGame, identity()));
    }

    @Override
    public String createNewGame(Setting setting) {
        return processors.get(setting.getGame()).createNewGame(setting);
    }

    @Override
    public void joinGame(String gameId) {
        findProcessor(gameId).joinGame(gameId);
    }

    @Override
    public void connect(String userId) {
        processors.values().forEach(processor -> processor.connect(userId));
    }

    @Override
    public void disconnect(String userId) {
        processors.values().forEach(processor -> processor.disconnect(userId));
    }

    @Override
    public void performAction(String gameId, String userId, Action<?, ?, ?, ?> action) throws ActionException {
        Processor<?, ?, ?, ?, ?> processor = findProcessor(gameId);

        if (!StringUtils.equals(processor.getGame(), action.getGame())) {
            throw new ActionException("Wrong game");
        }

        processor.performAction(gameId, userId, action);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Processor<?, ?, ?, ?, ?> findProcessor(String gameId) {
        return processors.values().stream()
            .filter(g -> g.containsGame(gameId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));
    }
}
