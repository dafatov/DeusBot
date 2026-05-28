package ru.demetrious.deus.bot.app.impl.game.codenames.domain;

import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team.SPECTATOR;

@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@Data
public class Player {
    private final String id;
    private Team team = SPECTATOR;
    private boolean captain;
    private String name;
    private String avatar;
    private CompletableFuture<Void> disconnectCompletableFuture;

    public enum Team {
        SPECTATOR, RED, BLUE
    }
}
