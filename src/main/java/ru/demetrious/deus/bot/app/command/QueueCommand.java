package ru.demetrious.deus.bot.app.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ButtonAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.component.ControlComponent;
import ru.demetrious.deus.bot.app.component.PaginationComponent;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.lang.Math.round;
import static java.lang.Math.toIntExact;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static ru.demetrious.deus.bot.fw.utils.PlayerUtils.getFormatDuration;
import static ru.demetrious.deus.bot.fw.utils.PlayerUtils.getPreview;

@Slf4j
@Component
public class QueueCommand extends PlayerCommand {
    public QueueCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("queue")
            .setDescription("Отображение очереди композиций на воспроизведение");
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        final Player player = getPlayer(slashCommandAdapter.getGuildId());
        final List<AudioTrack> queue = player.getQueue();
        MessageEmbed paginationEmbed = new MessageEmbed();
        MessageComponent controlMessageComponent = new ControlComponent(player, slashCommandAdapter.getAuthorId()).get();
        PaginationComponent paginationComponent = new PaginationComponent(queue.size());

        if (player.isNotPlaying()) {
            notifyIsNotPlaying(List.of(paginationComponent.get()), paginationComponent.getFooter(), slashCommandAdapter::notify);
            return;
        }

        MessageData messageData = updateEmbed(
            queue,
            paginationEmbed,
            paginationComponent,
            paginationComponent.get(),
            controlMessageComponent,
            player.getPlayingTrack()
        );

        slashCommandAdapter.notify(messageData);
        log.info("Список композиций успешно выведен");
    }

    @Override
    public void onButton(ButtonAdapter buttonAdapter) {
        final Player player = getPlayer(buttonAdapter.getGuildId());
        final List<AudioTrack> queue = player.getQueue();
        MessageEmbed paginationEmbed = buttonAdapter.getEmbed(0);
        MessageComponent controlMessageComponent = new ControlComponent(player, buttonAdapter.getAuthorId()).update(buttonAdapter.getCustomId());
        PaginationComponent paginationComponent = PaginationComponent.from(paginationEmbed.getFooter(), queue.size());

        if (player.isNotPlaying()) {
            notifyIsNotPlaying(List.of(paginationComponent.update(buttonAdapter.getCustomId())), paginationComponent.getFooter(), buttonAdapter::update);
            return;
        }

        MessageData messageData = updateEmbed(
            queue,
            paginationEmbed,
            paginationComponent,
            paginationComponent.update(buttonAdapter.getCustomId()),
            controlMessageComponent,
            player.getPlayingTrack()
        );

        buttonAdapter.update(messageData);
        log.debug("Список композиций успешно обновлен");
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateEmbed(List<AudioTrack> queue, MessageEmbed paginationEmbed, PaginationComponent paginationComponent,
                                    MessageComponent paginationMessageComponent, MessageComponent controlMessageComponent, AudioTrack playingTrack) {
        paginationEmbed
            .setTitle(playingTrack.getInfo().title)
            .setUrl(playingTrack.getInfo().uri)
            .setDescription(getDescription(playingTrack, queue, paginationComponent.getStart(), paginationComponent.getCount()))
            .setFooter(paginationComponent.getFooter());
        getPreview(playingTrack).ifPresent(paginationEmbed::setThumbnail);

        return new MessageData()
            .setEmbeds(List.of(paginationEmbed))
            .setComponents(List.of(paginationMessageComponent, controlMessageComponent));
    }

    private String getDescription(AudioTrack playingTrack, List<AudioTrack> queue, int start, int count) {
        if (playingTrack.getInfo().isStream) {
            return "<Стрим>\n\u200B\\n";
        }

        Stream<String> playing = of(getPlaying(playingTrack));
        Stream<String> songs = queue.stream()
            .skip(start)
            .map(audioTrack -> format("{0}. [{1}]({2})\n`{3}`—_<@{4}>_",
                leftPad(String.valueOf(queue.indexOf(audioTrack) + 1), String.valueOf(queue.size()).length(), "0"),
                audioTrack.getInfo().title,
                audioTrack.getInfo().uri,
                getFormatDuration(audioTrack.getDuration()),
                audioTrack.getUserData()
            )).limit(count);

        return concat(playing, songs).collect(joining("\n\n"));
    }

    private String getPlaying(AudioTrack playingTrack) {
        long position = playingTrack.getPosition();
        long duration = playingTrack.getDuration();
        int percent = toIntExact(round(100D * position / duration));

        return format("`{0}`/`{1}`—_<@{2}>_\n{3} [{4}%]",
            getFormatDuration(position),
            getFormatDuration(duration),
            playingTrack.getUserData(),
            getProgressBar(percent),
            percent
        );
    }

    private String getProgressBar(int percent) {
        int length = 40;
        int count = percent * length / 100;

        return "■".repeat(count) + "□".repeat(length - count);
    }
}
