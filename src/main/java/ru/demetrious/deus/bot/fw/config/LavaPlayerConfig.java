package ru.demetrious.deus.bot.fw.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Android;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.TvHtml5Embedded;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.demetrious.deus.bot.app.player.source.client.Web;

@Configuration
public class LavaPlayerConfig {
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        DefaultAudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

        audioPlayerManager.registerSourceManagers(getAudioSourceManagers());

        return audioPlayerManager;
    }

    @Bean
    public AudioPlayer audioPlayer() {
        return audioPlayerManager().createPlayer();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private AudioSourceManager[] getAudioSourceManagers() {
        return new AudioSourceManager[]{
            new YoutubeAudioSourceManager(new Music(), new Web(), new Android(), new TvHtml5Embedded()),
            new HttpAudioSourceManager()
        };
    }
}
