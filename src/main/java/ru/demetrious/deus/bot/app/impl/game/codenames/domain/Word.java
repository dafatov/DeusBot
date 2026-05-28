package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@EqualsAndHashCode(of = "text")
@Data
public class Word {
    private final String text;
    private final Color color;
    private boolean revealed = false;

    public enum Color {
        RED, BLUE, WHITE, BLACK
    }
}
