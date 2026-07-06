package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;

@Accessors(chain = true)
@EqualsAndHashCode(of = "text")
@Data
public class Word {
    private final String text;
    private final Color color;
    private Reveal revealed;

    public record Reveal(int order, Team team, int round) {
    }

    public enum Color {
        RED, BLUE, WHITE, BLACK
    }
}
