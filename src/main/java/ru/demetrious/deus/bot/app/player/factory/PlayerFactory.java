package ru.demetrious.deus.bot.app.player.factory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import ru.demetrious.deus.bot.adapter.inbound.jda.handler.AudioSendHandlerImpl;
import ru.demetrious.deus.bot.app.player.PlayerImpl;
import ru.demetrious.deus.bot.app.player.TrackScheduler;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.handler.AudioLoadResultHandlerImpl;

// TODO: тот же гигантский костыль (что и в CommandAdapterFactory), который я уверен не обязателен и можно сделать по уму
public class PlayerFactory {
    public static Player create() {
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        Player player = new PlayerImpl(
            audioPlayerManager,
            new AudioLoadResultHandlerImpl(new TrackScheduler(audioPlayer)),
            new AudioSendHandlerImpl(audioPlayer)
        );

        audioPlayerManager.registerSourceManagers(
            new YoutubeAudioSourceManager(),
            new HttpAudioSourceManager()
        );

        return player;
    }
}
