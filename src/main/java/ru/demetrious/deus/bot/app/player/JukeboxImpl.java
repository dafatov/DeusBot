package ru.demetrious.deus.bot.app.player;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;

import static ru.demetrious.deus.bot.app.player.factory.PlayerFactory.create;

@RequiredArgsConstructor
@Component
public class JukeboxImpl implements Jukebox {
    private final Map<String, Player> players = new HashMap<>();

    @Override
    public Player getPlayer(String guildId) {
        return players.computeIfAbsent(guildId, key -> create());
    }
}
