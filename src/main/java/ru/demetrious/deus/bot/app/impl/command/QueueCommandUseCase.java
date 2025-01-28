package ru.demetrious.deus.bot.app.impl.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.QueueCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.user.GetAuthorIdOutbound;
import ru.demetrious.deus.bot.app.impl.component.ControlComponent;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;
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
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static ru.demetrious.deus.bot.domain.CommandData.Name.QUEUE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.INFO;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;
import static ru.demetrious.deus.bot.utils.PlayerUtils.getFormatDuration;
import static ru.demetrious.deus.bot.utils.PlayerUtils.getPreview;

@RequiredArgsConstructor
@Slf4j
@Component
public class QueueCommandUseCase extends PlayerCommand implements QueueCommandInbound {
    private final List<GetAuthorIdOutbound<?>> getAuthorIdOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(QUEUE)
            .setDescription("Отображение очереди композиций на воспроизведение");
    }

    @Override
    public void onButton() {
        final Player player = getPlayer();
        Result<List<AudioTrack>> result = player.getQueue();
        MessageEmbed paginationEmbed = getEmbedOutbound.getEmbed(0);
        MessageComponent controlMessageComponent = new ControlComponent(player, b(getAuthorIdOutbound).getAuthorId()).update(getCustomIdOutbound.getCustomId());
        PaginationComponent paginationComponent = PaginationComponent.from(paginationEmbed.getFooter(), emptyIfNull(result.getData()).size());

        switch (result.getStatus()) {
            case IS_NOT_PLAYING -> notifyIsNotPlaying(List.of(paginationComponent.update(getCustomIdOutbound.getCustomId())), paginationComponent.getFooter());
            case OK -> {
                MessageData messageData = updateEmbed(
                    result.getData(),
                    paginationEmbed,
                    paginationComponent,
                    paginationComponent.update(getCustomIdOutbound.getCustomId()),
                    controlMessageComponent,
                    player.getPlayingTrack()
                );

                b(notifyOutbound).notify(messageData);
                log.debug("Список композиций успешно обновлен");
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(result.getStatus()));
        }
    }

    @Override
    public void execute() {
        final Player player = getPlayer();
        Result<List<AudioTrack>> result = player.getQueue();
        MessageEmbed paginationEmbed = new MessageEmbed();
        MessageComponent controlMessageComponent = new ControlComponent(player, b(getAuthorIdOutbound).getAuthorId()).get();
        PaginationComponent paginationComponent = new PaginationComponent(emptyIfNull(result.getData()).size());

        switch (result.getStatus()) {
            case IS_NOT_PLAYING -> notifyIsNotPlaying(List.of(paginationComponent.get()), paginationComponent.getFooter());
            case OK -> {
                MessageData messageData = updateEmbed(
                    result.getData(),
                    paginationEmbed,
                    paginationComponent,
                    paginationComponent.get(),
                    controlMessageComponent,
                    player.getPlayingTrack()
                );

                b(notifyOutbound).notify(messageData);
                log.info("Список композиций успешно выведен");
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(result.getStatus()));
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateEmbed(List<AudioTrack> queue, MessageEmbed paginationEmbed, PaginationComponent paginationComponent,
                                    MessageComponent paginationMessageComponent, MessageComponent controlMessageComponent, AudioTrack playingTrack) {
        paginationEmbed
            .setColor(INFO)
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
        return concat(of(getPlaying(playingTrack)), queue.stream()
            .skip(start)
            .map(audioTrack -> mapAudioTrack(audioTrack, queue.indexOf(audioTrack), queue.size()))
            .limit(count))
            .collect(joining("\n\n"));
    }

    private String mapAudioTrack(AudioTrack audioTrack, int index, int size) {
        return format("{0}. [{1}]({2})\n`{3}`—_<@{4}>_",
            leftPad(String.valueOf(index + 1), String.valueOf(size).length(), "0"),
            audioTrack.getInfo().title,
            audioTrack.getInfo().uri,
            audioTrack.getInfo().isStream ? "<Стрим>" : getFormatDuration(audioTrack.getDuration()),
            audioTrack.getUserData()
        );
    }

    private String getPlaying(AudioTrack playingTrack) {
        if (playingTrack.getInfo().isStream) {
            return format("`<Стрим>`—_<@{0}>_", playingTrack.getUserData());
        }

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
