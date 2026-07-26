package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Hint;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.FINISHED;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeNamesInstance extends Instance<CodeNamesSetting, CodeNamesPlayer> {
    private final State state = new State();
    private final Set<Word> wordList = new HashSet<>();
    private final List<Hint> hintList = new LinkedList<>();
    private final Map<String, Vote> voteMap = new HashMap<>();

    public CodeNamesInstance(String key, String hostId, CodeNamesSetting setting) {
        super(key, hostId, setting);
    }

    @Override
    public boolean isFinished() {
        return state.getPhase() == FINISHED;
    }
}
