package ru.demetrious.deus.bot.app.player.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

@Slf4j
@RequiredArgsConstructor
@Component
public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {
    private final Scheduler scheduler;

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        scheduler.enqueue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        audioPlaylist.getTracks().forEach(scheduler::enqueue);
    }

    @Override
    public void noMatches() {
        log.warn("Load no matches");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        log.error("Load failed", e);
    }
}
