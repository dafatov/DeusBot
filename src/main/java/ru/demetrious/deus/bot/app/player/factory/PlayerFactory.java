package ru.demetrious.deus.bot.app.player.factory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.PlayerImpl;
import ru.demetrious.deus.bot.app.player.SchedulerImpl;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.handler.AudioEventAdapterImpl;

@RequiredArgsConstructor
@Component
public class PlayerFactory {
    private final AudioPlayerManager audioPlayerManager;

    public Player create() {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        SchedulerImpl scheduler = new SchedulerImpl(audioPlayer);

        audioPlayer.addListener(new AudioEventAdapterImpl(scheduler));

        return new PlayerImpl(
            audioPlayerManager,
            audioPlayer,
            scheduler
        );
    }
}
