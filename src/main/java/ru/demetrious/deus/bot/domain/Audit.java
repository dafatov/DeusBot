package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@Accessors(chain = true)
public class Audit {
    @EmbeddedId
    private AuditId auditId;
    private Long count;
    @CreationTimestamp
    private Instant created;
    @UpdateTimestamp
    private Instant lastModified;

    public enum Type {
        BUTTON,
        COMMAND,
        MESSAGE,
        MODAL,
        VOICE
    }

    @Data
    @Embeddable
    @Accessors(chain = true)
    public static class AuditId implements Serializable {
        private String guildId;
        private String userId;
        @Enumerated(STRING)
        private Type type;
        private String name;
    }
}
