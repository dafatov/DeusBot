package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class Publicist {
    @Id
    private String guildId;
    private String channelId;
}
