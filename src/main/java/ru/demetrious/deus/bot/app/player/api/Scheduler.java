package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public interface Scheduler {
    void enqueue(AudioTrack audioTrack);

    void next();

    List<AudioTrack> getQueue();

    void clear();

    boolean setLoop(boolean isLoop);

    boolean getLoop();

    AudioTrack move(Integer target, Integer position);
}
