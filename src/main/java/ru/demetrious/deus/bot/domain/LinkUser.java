package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class LinkUser {
    @EmbeddedId
    private LinkUserKey linkUserKey;
    private String linkedPrincipalName;

    @Data
    @Embeddable
    @Accessors(chain = true)
    public static class LinkUserKey implements Serializable {
        private String discordPrincipalName;
        private String linkedRegistrationId;
    }
}
