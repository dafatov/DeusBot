package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.crossward.utils.CrosswordUtils.tryPlaceWord;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class CrossWardInstance extends Instance<CrossWardSetting, CrossWardPlayer> {
    private final List<CrossWardPlayer> activePlayers = new ArrayList<>();
    private final State state = new State();
    private final Queue<String> availableWords = new ArrayDeque<>();
    private final Map<Tag, Set<Character>> letterTags = new EnumMap<>(Tag.class);
    private final Map<Position, Cell> grid = new HashMap<>();
    private final List<Word> words = new ArrayList<>();

    public CrossWardInstance(String key, String hostId, CrossWardSetting setting) {
        super(key, setting, hostId);
    }

    @Override
    public boolean isFinished() {
        return state.getPhase() == FINISHED;
    }

    @Override
    public void removePlayer(CrossWardPlayer player) {
        activePlayers.remove(player);
        super.removePlayer(player);
    }

    public void placeStartWords() {
        placeWord(3, null, true);
    }

    public void placeWord() {
        placeWord(1, state.getCurrentPlayer(), false);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    @Synchronized
    private void placeWord(int count, @Nullable CrossWardPlayer owner, boolean revealed) {
        AtomicInteger index = new AtomicInteger();

        while (index.get() < count) {
            String text = getAvailableWords().poll();

            tryPlaceWord(text, getWords(), getGrid())
                .ifPresentOrElse(word -> {
                    log.trace("[Added] {}", word.getText());
                    index.getAndIncrement();
                    word.setOwner(owner);
                    word.setOrder(getWords().size());
                    word.setRevealed(revealed);
                    word.getCells().forEach(cell -> cell.setRevealed(cell.isRevealed() || revealed));
                }, () -> getAvailableWords().add(text));
        }
    }
}
