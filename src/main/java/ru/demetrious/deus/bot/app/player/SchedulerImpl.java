package ru.demetrious.deus.bot.app.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

@RequiredArgsConstructor
@Component
public class SchedulerImpl implements Scheduler {
    private final List<AudioTrack> queue = new LinkedList<>();
    private final AudioPlayer audioPlayer;

    private boolean isLoop;

    @Override
    public void enqueue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            queue.add(audioTrack);
        }
    }

    @Override
    public void next() {
        audioPlayer.startTrack(queue.remove(0), false);
    }

    @Override
    public List<AudioTrack> getQueue() {
        return queue;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean setLoop(boolean isLoop) {
        return this.isLoop = isLoop;
    }

    @Override
    public boolean getLoop() {
        return isLoop;
    }

    @Override
    public AudioTrack move(Integer target, Integer position) {
        AudioTrack audioTrack = queue.remove(target.intValue());

        queue.add(position, audioTrack);
        return audioTrack;
    }
}
