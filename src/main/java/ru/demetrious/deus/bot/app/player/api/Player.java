package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;

public interface Player {
    void connect(GenericInteractionAdapter genericInteractionAdapter);

    Optional<AudioItem> add(AudioReference reference, String userId);

    List<AudioTrack> getQueue();

    Long getRemaining();

    void clear();

    boolean isNotPlaying();

    boolean isPlayingLive();

    boolean loop();

    boolean isLooped();

    boolean isPaused();

    boolean isValidIndex(Integer index);

    AudioTrack move(Integer target, Integer position);

    boolean pause();

    AudioTrack getPlayingTrack();

    void skip();

    void shuffle();
}
