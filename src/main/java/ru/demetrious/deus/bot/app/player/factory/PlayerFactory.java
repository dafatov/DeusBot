package ru.demetrious.deus.bot.app.player.factory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.handler.AudioSendHandlerImpl;
import ru.demetrious.deus.bot.app.player.PlayerImpl;
import ru.demetrious.deus.bot.app.player.TrackScheduler;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.handler.AudioLoadResultHandlerImpl;

// TODO: тот же костыль (что и в CommandAdapterFactory)?
@RequiredArgsConstructor
@Component
public class PlayerFactory {
    private final AudioPlayerManager audioPlayerManager;

    public Player create() {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();

        return new PlayerImpl(
            audioPlayerManager,
            new AudioLoadResultHandlerImpl(new TrackScheduler(audioPlayer)),
            new AudioSendHandlerImpl(audioPlayer)
        );
    }
}
