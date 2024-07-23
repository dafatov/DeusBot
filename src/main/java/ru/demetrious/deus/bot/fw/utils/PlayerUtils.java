package ru.demetrious.deus.bot.fw.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerUtils {
    public static Long reduceDuration(List<AudioTrack> audioTrackList) {
        return audioTrackList.stream().reduce(0L, (duration, audioTrack) -> duration + audioTrack.getDuration(), Long::sum);
    }

    public static boolean hasLive(List<AudioTrack> audioTrackList) {
        return audioTrackList.stream().anyMatch(audioTrack -> audioTrack.getInfo().isStream);
    }
}
