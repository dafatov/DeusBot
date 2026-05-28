package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import com.google.common.collect.Multiset;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote;

import static com.google.common.collect.EnumMultiset.create;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.WAITING;

@EqualsAndHashCode(of = "key")
@RequiredArgsConstructor
@Data
public class GameSession {
    private final String key;
    private final String hostId;
    private final Setting setting;
    private final State state = new State();
    private final Set<Player> playerList = new HashSet<>();
    private final Set<Word> wordList = new HashSet<>();
    private final List<Hint> hintList = new LinkedList<>();
    private final Map<String, Vote> voteMap = new HashMap<>();

    @Data
    public static class State {
        private final Multiset<Team> score = create(Team.class);
        private Phase phase = WAITING;
        private Team team;
        private Instant timer;
        private Duration remaining;
        private CompletableFuture<Void> timerCompletableFuture;
        private Runnable timerTask;
        private boolean locked = false;

        public enum Phase {
            WAITING, HINTING, GUESSING, FINISHED
        }
    }
}
