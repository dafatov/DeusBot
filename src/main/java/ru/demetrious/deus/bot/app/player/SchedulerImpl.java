package ru.demetrious.deus.bot.app.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

@RequiredArgsConstructor
@Component
public class SchedulerImpl implements Scheduler {
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private final AudioPlayer audioPlayer;

    @Override
    public void enqueue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            queue.offer(audioTrack);
        }
    }

    @Override
    public void next() {
        audioPlayer.startTrack(queue.poll(), false);
    }

    @Override
    public List<AudioTrack> getQueue() {
        return queue.stream().toList();
    }
}
