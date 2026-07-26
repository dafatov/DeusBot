package ru.demetrious.deus.bot.app.impl.game.common.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.impl.game.common.Processor;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
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
    public Optional<Pair<? extends Instance<?, ?>, ? extends Player>> findByPlayer(String userId) {
        return processors.values().stream()
            .map(f -> f.findPlayer(userId))
            .findFirst()
            .flatMap(identity());
    }

    @Override
    public void performAction(String gameId, String userId, Action<?, ?, ?, ?> action) throws ActionException {
        Processor<?, ?, ?, ?, ?> processor = findProcessor(gameId);

        if (!StringUtils.equals(processor.getGame(), action.getGame())) {
            throw new ActionException("Wrong game");
        }

        processor.performAction(gameId, userId, action);
    }

    @Override
    public void removeGame(String gameId) {
        findProcessor(gameId).removeGame(gameId);
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
