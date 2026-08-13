package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.FINISHED;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrossWardInstance extends Instance<CrossWardSetting, CrossWardPlayer> {
    private final List<CrossWardPlayer> activePlayers = new ArrayList<>();
    private final State state = new State();
    private final Queue<String> availableWords = new ArrayDeque<>();
    private final Map<Position, Cell> grid = new HashMap<>();
    private final List<Word> words = new ArrayList<>();

    public CrossWardInstance(String key, String hostId, CrossWardSetting setting) {
        super(key, hostId, setting);
    }

    @Override
    public boolean isFinished() {
        return state.getPhase() == FINISHED;
    }

    @Override
    public void removePlayer(String userId) {
        activePlayers.removeIf(p -> p.getId().equals(userId));
        super.removePlayer(userId);
    }
}
