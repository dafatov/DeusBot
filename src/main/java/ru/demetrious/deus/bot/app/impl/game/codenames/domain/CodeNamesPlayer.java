package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.SPECTATOR;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeNamesPlayer extends Player {
    private Team team = SPECTATOR;
    private boolean captain;

    public CodeNamesPlayer(String id, String name, String avatar) {
        super(id, name, avatar);
    }

    public enum Team {
        SPECTATOR, RED, BLUE
    }
}
