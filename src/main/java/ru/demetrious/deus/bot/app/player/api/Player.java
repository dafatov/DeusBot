package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public interface Player {
    AudioSendHandler getAudioSendHandler();

    void add(String identifier);

    List<AudioTrack> getQueue();
}
