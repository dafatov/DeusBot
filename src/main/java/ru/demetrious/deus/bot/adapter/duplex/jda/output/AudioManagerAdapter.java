package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.player.DisconnectOutbound;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class AudioManagerAdapter implements DisconnectOutbound {
    private final JDA jda;

    @Override
    public void disconnect(String guildId) {
        ofNullable(jda.getGuildById(guildId))
            .map(Guild::getAudioManager)
            .orElseThrow(() -> new IllegalStateException("Guild %s has no audio manager".formatted(guildId)))
            .closeAudioConnection();
    }
}
