package ru.demetrious.deus.bot.app.player.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

@RequiredArgsConstructor
@Component
public class AudioEventAdapterImpl extends AudioEventAdapter {
    private final Scheduler scheduler;

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            scheduler.next();
        }
    }
}
