package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public interface Scheduler {
    void enqueue(AudioTrack audioTrack);

    AudioTrack next();

    List<AudioTrack> getQueue();

    void clear();

    boolean setLooped(boolean isLooped);

    boolean isLooped();

    boolean isPaused();

    AudioTrack move(Integer target, Integer position);

    void shuffle();

    AudioTrack remove(Integer target);
}
