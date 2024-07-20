package ru.demetrious.deus.bot.app.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Player;

@RequiredArgsConstructor
@Component
public class PlayerImpl implements Player {
    private final AudioPlayerManager audioPlayerManager;
    private final AudioLoadResultHandler audioLoadResultHandler;
    private final AudioSendHandler audioSendHandler;

    @Override
    public AudioSendHandler getAudioSendHandler() {
        return audioSendHandler;
    }

    @Override
    public void add(String identifier) {
        audioPlayerManager.loadItem(identifier, audioLoadResultHandler);
    }
}
