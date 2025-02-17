package ru.demetrious.deus.bot.fw.config.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.TvHtml5Embedded;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.demetrious.deus.bot.app.impl.player.impl.client.Web;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
public class LavaPlayerConfig {
    @Value("${youtube.refresh-token}")
    private String youtubeRefreshToken;

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
        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(new Music(), new Web(), new TvHtml5Embedded());

        youtubeAudioSourceManager.useOauth2(youtubeRefreshToken, isNotBlank(youtubeRefreshToken));

        return new AudioSourceManager[]{
            youtubeAudioSourceManager,
            new HttpAudioSourceManager()
        };
    }
}
