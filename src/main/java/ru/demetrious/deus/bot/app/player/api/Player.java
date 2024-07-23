package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public interface Player {
    void connect(VoiceChannel voiceChannel, AudioManager audioManager);

    Optional<AudioItem> add(AudioReference reference);

    List<AudioTrack> getQueue();

    Long getRemaining();
}
