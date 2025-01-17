package ru.demetrious.deus.bot.app.impl.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;

public interface Player {
    void connect();

    Optional<AudioItem> add(AudioReference reference, String userId);

    List<AudioTrack> getQueue();

    Long getRemaining();

    void clear();

    boolean isNotPlaying();

    boolean isPlayingLive();

    boolean loop();

    boolean isLooped();

    boolean isPaused();

    boolean isNotValidIndex(Integer index);

    boolean isValidIndex(Integer index);

    AudioTrack move(Integer target, Integer position);

    boolean pause();

    AudioTrack getPlayingTrack();

    AudioTrack skip();

    void shuffle();

    AudioTrack remove(Integer target);
}
