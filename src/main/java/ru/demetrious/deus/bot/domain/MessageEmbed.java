package ru.demetrious.deus.bot.domain;

import java.awt.Color;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageEmbed {
    private String title;
    private String description;
    private Instant timestamp;
    private Color color;
}
