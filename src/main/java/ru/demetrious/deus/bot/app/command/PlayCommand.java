package ru.demetrious.deus.bot.app.command;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.track.YoutubeAudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.OnModalAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.domain.AddedInfo;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.text.MessageFormat.format;
import static java.util.stream.IntStream.rangeClosed;
import static net.dv8tion.jda.api.entities.Message.Attachment;
import static net.dv8tion.jda.api.interactions.components.text.TextInputStyle.SHORT;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationWords;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.ATTACHMENT;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.fw.utils.PlayerUtils.hasLive;
import static ru.demetrious.deus.bot.fw.utils.PlayerUtils.reduceDuration;

@Slf4j
@Component
public class PlayCommand extends PlayerCommand {
    private static final String STRING_OPTION_DESCRIPTION = "Url или наименование аудио";
    private static final String NOT_FOUND_DESCRIPTION = "По данному запросу ничего не найдено";

    public PlayCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("play")
            .setDescription("Ну типа play")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName("string")
                    .setDescription(STRING_OPTION_DESCRIPTION),
                new OptionData()
                    .setType(ATTACHMENT)
                    .setName("attachment")
                    .setDescription("Файл с аудиозаписью")
            ));
    }

    @Override
    public void execute(CommandAdapter commandAdapter) {
        Optional<String> identifierOptional = commandAdapter.getStringOption("string");
        Optional<Attachment> attachmentOptional = commandAdapter.getAttachmentOption("attachment");

        if (identifierOptional.isPresent() && attachmentOptional.isPresent()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Некорректное состояние команды \"play\"")
                .setDescription("Обратитесь к администратору для выяснения обстоятельств")));

            commandAdapter.notify(messageData);
            log.warn("Некорректное состояние команды \"play\"");
            return;
        }

        if (commandAdapter.isUnequalChannels()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Канал не тот")
                .setDescription("Мда.. шиза.. перепутать каналы это надо уметь")));

            commandAdapter.notify(messageData);
            log.warn("Не совпадают каналы");
            return;
        }

        if (identifierOptional.isEmpty() && attachmentOptional.isEmpty()) {
            Modal modal = Modal.create("play", "Добавление в очередь плеера")
                .addComponents(rangeClosed(1, Modal.MAX_COMPONENTS)
                    .mapToObj(index -> ActionRow.of(TextInput.create(String.valueOf(index), "#" + index, SHORT)
                        .setPlaceholder(STRING_OPTION_DESCRIPTION)
                        .setRequired(index == 1)
                        .build()))
                    .toList())
                .build();

            commandAdapter.showModal(modal);
            log.info("Успешно открыто модальное окно для добавления композиций в очередь");
            return;
        }

        final Player player = getPlayer(commandAdapter);
        int queueSize = player.getQueue().size();
        boolean hasLive = hasLive(player.getQueue());
        Long remained = player.getRemaining();

        Optional<AudioItem> addedOptional = player.add(identifierOptional
            .map(identifier -> new AudioReference(identifier, identifier))
            .or(() -> attachmentOptional
                .map(attachment -> new AudioReference(attachment.getUrl(), attachment.getFileName())))
            .get());

        if (addedOptional.isEmpty()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Душный запрос")
                .setDescription(NOT_FOUND_DESCRIPTION)));

            commandAdapter.notify(messageData);
            log.info(NOT_FOUND_DESCRIPTION);
            return;
        }

        player.connect(commandAdapter);

        AddedInfo added = getAddedInfo(addedOptional.get());
        String description = getDescription(added, queueSize, getRemained(hasLive, remained));

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle(added.getTitle())
            .setDescription(description)
            .setUrl(added.getUrl())
            .setThumbnail(added.getPreview())));

        commandAdapter.notify(messageData);
        log.info("Композиция успешно добавлена в очередь");
    }

    @Override
    public void onModal(OnModalAdapter onModalAdapter) {
        List<String> values = onModalAdapter.getValues();

        log.debug("values: {}", values);
    }

    @Override
    public boolean isDeferReply(CommandAdapter commandAdapter) {
        return commandAdapter.getStringOption("string").isPresent() || commandAdapter.getAttachmentOption("attachment").isPresent();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private String getDescription(AddedInfo added, int length, String remained) {
        String duration = added.isLive() ? "<Стрим>" : formatDurationWords(added.getDuration(), true, true);

        return format(
            """
                Количество композиций: **{0}**
                Длительность: **{1}**
                Место в очереди: **{2}**
                Начнется через: **{3}**
                """,
            added.getCount(),
            duration,
            length + 1,
            remained
        );
    }

    private String getRemained(boolean hasLive, long remained) {
        if (hasLive) {
            return "<Никогда>";
        }

        if (remained == 0) {
            return "<Сейчас>";
        }

        return formatDurationWords(remained, true, true);
    }

    private AddedInfo getAddedInfo(AudioItem audioItem) {
        if (audioItem instanceof AudioTrack audioTrack) {
            AddedInfo addedInfo = new AddedInfo()
                .setTitle(audioTrack.getInfo().title)
                .setUrl(audioTrack.getInfo().uri)
                .setDuration(audioTrack.getDuration())
                .setLive(audioTrack.getInfo().isStream);

            setPreview(audioTrack, addedInfo);
            return addedInfo;
        } else if (audioItem instanceof AudioPlaylist audioPlaylist) {
            AddedInfo addedInfo = new AddedInfo()
                .setCount(audioPlaylist.getTracks().size())
                .setTitle(audioPlaylist.getName())
                .setDuration(reduceDuration(audioPlaylist.getTracks()))
                .setLive(hasLive(audioPlaylist.getTracks()));

            audioPlaylist.getTracks().stream().findFirst()
                .ifPresent(audioTrack -> setPreview(audioTrack, addedInfo));
            return addedInfo;
        } else {
            throw new IllegalStateException("Unknown audioItem class: " + audioItem.getClass());
        }
    }

    private static void setPreview(AudioTrack audioTrack, AddedInfo addedInfo) {
        if (audioTrack instanceof YoutubeAudioTrack youtubeAudioTrack) {
            addedInfo.setPreview(format("https://i3.ytimg.com/vi/{0}/hqdefault.jpg", youtubeAudioTrack.getIdentifier()));
        }
    }
}
