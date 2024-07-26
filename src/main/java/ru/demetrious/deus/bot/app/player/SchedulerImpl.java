package ru.demetrious.deus.bot.app.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

@RequiredArgsConstructor
@Component
public class SchedulerImpl implements Scheduler {
    private final List<AudioTrack> queue = new LinkedList<>();
    private final AudioPlayer audioPlayer;

    private boolean isLooped;

    @Override
    public void enqueue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            queue.add(audioTrack);
        }
    }

    @Override
    public void next() {
        if (queue.isEmpty()) {
            audioPlayer.stopTrack();
        } else {
            audioPlayer.startTrack(remove(0), false);
        }
    }

    @Override
    public List<AudioTrack> getQueue() {
        return queue;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    public boolean setLooped(boolean isLooped) {
        return this.isLooped = isLooped;
    }

    @Override
    public boolean isLooped() {
        return isLooped;
    }

    @Override
    public boolean isPaused() {
        return audioPlayer.isPaused();
    }

    @Override
    public AudioTrack move(Integer target, Integer position) {
        AudioTrack audioTrack = remove(target);

        queue.add(position, audioTrack);
        return audioTrack;
    }

    @Override
    public void shuffle() {
        throw new NotImplementedException("Shuffle not implemented");
    }

    @Override
    public AudioTrack remove(Integer target) {
        return queue.remove(target.intValue());
    }
}
