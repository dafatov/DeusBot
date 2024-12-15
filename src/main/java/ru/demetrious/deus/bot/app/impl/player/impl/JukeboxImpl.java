package ru.demetrious.deus.bot.app.impl.player.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.impl.player.api.Jukebox;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.app.impl.player.impl.factory.PlayerFactory;

@RequiredArgsConstructor
@Component
public class JukeboxImpl implements Jukebox {
    private final Map<String, Player> players = new HashMap<>();
    private final PlayerFactory playerFactory;

    @Override
    public Player getPlayer(String guildId) {
        return players.computeIfAbsent(guildId, playerFactory::create);
    }
}
