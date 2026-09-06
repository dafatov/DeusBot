package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.SPECTATOR;

@Accessors(chain = true)
@Data
public class CodeNamesPlayer extends Player {
    private Team team = SPECTATOR;
    private boolean captain;

    public CodeNamesPlayer(String id, String name, String avatar) {
        super(id, name, avatar);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public enum Team {
        SPECTATOR, RED, BLUE
    }
}
