package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;

@Accessors(chain = true)
@EqualsAndHashCode(of = {"word", "team"})
@Data
public class Hint {
    private final String word;
    private final Team team;
    private final int count;
    private int guessed;
}
