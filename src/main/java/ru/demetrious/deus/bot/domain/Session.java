package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static jakarta.persistence.EnumType.STRING;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.NONE;
import static ru.demetrious.deus.bot.domain.Session.State.UNRELIABLE_FINISH;
import static ru.demetrious.deus.bot.domain.Session.State.UNRELIABLE_START;

@Data
@Entity
@Accessors(chain = true)
public class Session {
    @EmbeddedId
    private SessionId id;
    @Setter(NONE)
    @Column(nullable = false)
    private Instant start;
    @Setter(NONE)
    private Instant finish;
    @Getter(NONE)
    @Setter(NONE)
    @Enumerated(STRING)
    @Column(nullable = false)
    private Set<State> states = new HashSet<>();

    public void start(Instant now, boolean isForced) {
        start = now;
        finish = null;
        updateState(UNRELIABLE_START, isForced);
        updateState(UNRELIABLE_FINISH, false);
    }

    public void finish(Instant now, boolean isForced) {
        start = ofNullable(start).orElse(now);
        finish = now;
        updateState(UNRELIABLE_FINISH, isForced);
    }

    public boolean inState(State state) {
        return states.contains(state);
    }

    @Data
    @Embeddable
    @Accessors(chain = true)
    public static class SessionId implements Serializable {
        private String guildId;
        private String userId;
    }

    public enum State {
        UNRELIABLE_START,
        UNRELIABLE_FINISH
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void updateState(State state, boolean isActual) {
        if (isActual) {
            states.add(state);
        } else {
            states.remove(state);
        }
    }
}
