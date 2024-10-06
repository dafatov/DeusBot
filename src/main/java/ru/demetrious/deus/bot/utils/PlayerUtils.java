package ru.demetrious.deus.bot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.track.YoutubeAudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@NoArgsConstructor(access = PRIVATE)
public class PlayerUtils {
    public static Long reduceDuration(List<AudioTrack> audioTrackList) {
        return audioTrackList.stream().reduce(0L, (duration, audioTrack) -> duration + audioTrack.getDuration(), Long::sum);
    }

    public static boolean hasLive(List<AudioTrack> audioTrackList) {
        return audioTrackList.stream().anyMatch(audioTrack -> audioTrack.getInfo().isStream);
    }

    public static Optional<String> getPreview(AudioTrack audioTrack) {
        if (audioTrack instanceof YoutubeAudioTrack youtubeAudioTrack) {
            return Optional.of(format("https://i3.ytimg.com/vi/{0}/hqdefault.jpg", youtubeAudioTrack.getIdentifier()));
        }

        return empty();
    }

    @NotNull
    public static String getFormatDuration(long position) {
        return formatDuration(position, "HH:mm:ss", true);
    }
}
