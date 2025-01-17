package ru.demetrious.deus.bot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Log {
    @Id
    private UUID id;
    @Column(length = 0)
    private String exception;
    private String level;
    private String logger;
    @Column(length = 0)
    private String message;
    private Instant timestamp;
}
