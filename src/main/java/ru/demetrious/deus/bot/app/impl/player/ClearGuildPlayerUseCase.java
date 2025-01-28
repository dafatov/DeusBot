package ru.demetrious.deus.bot.app.impl.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.player.ClearGuildPlayerInbound;
import ru.demetrious.deus.bot.app.impl.player.api.Jukebox;
import ru.demetrious.deus.bot.app.impl.player.api.Player;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClearGuildPlayerUseCase implements ClearGuildPlayerInbound {
    private final Jukebox jukebox;

    @Override
    public void execute(String guildId) {
        final Player player = jukebox.getPlayer(guildId);

        player.clear(true);
        player.skip(true);
        log.info("Успешно очищен список композиций и текущая после выхода из голосового сервера {}", guildId);
    }
}
