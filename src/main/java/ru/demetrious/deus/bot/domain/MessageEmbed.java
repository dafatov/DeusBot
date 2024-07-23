package ru.demetrious.deus.bot.domain;

import java.awt.Color;
import java.time.Instant;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.time.Instant.now;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.INFO;

@Data
@Accessors(chain = true)
public class MessageEmbed {
    private String title;
    private String description;
    private String url;
    private String thumbnail;
    private Instant timestamp = now();
    private ColorEnum color = INFO;

    @Getter
    @RequiredArgsConstructor
    public enum ColorEnum {
        INFO(new Color(255, 255, 80)),
        WARNING(new Color(255, 136, 0)),
        ERROR(new Color(255, 0, 0));

        private final Color value;
    }
}
