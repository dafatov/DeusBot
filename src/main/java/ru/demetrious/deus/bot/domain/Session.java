package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class Session {
    @EmbeddedId
    private SessionId id;
    @Column(nullable = false)
    private Instant start;
    private Instant finish;

    @Data
    @Embeddable
    @Accessors(chain = true)
    public static class SessionId implements Serializable {
        private String guildId;
        private String userId;
    }
}
