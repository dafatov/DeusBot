package ru.demetrious.deus.bot.fw.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            new YoutubeAudioSourceManager(),
            new HttpAudioSourceManager()
        };
    }
}
