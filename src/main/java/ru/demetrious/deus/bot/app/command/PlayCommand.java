package ru.demetrious.deus.bot.app.command;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.domain.AddedInfo;
import ru.demetrious.deus.bot.app.player.domain.YoutubeAudioPlaylist;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.TextInputComponent;
import ru.demetrious.deus.bot.fw.utils.PlayerUtils;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationWords;
import static ru.demetrious.deus.bot.adapter.inbound.jda.mapper.ModalDataMapper.MAX_COMPONENTS;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.ATTACHMENT;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.domain.TextInputComponent.StyleEnum.SHORT;
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
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        play(slashCommandAdapter, slashCommandAdapter.getStringOption("string"),
            slashCommandAdapter.getAttachmentOption("attachment"), slashCommandAdapter::showModal);
    }

    @Override
    public boolean isDeferReply(SlashCommandAdapter slashCommandAdapter) {
        return slashCommandAdapter.getStringOption("string").isPresent() || slashCommandAdapter.getAttachmentOption("attachment").isPresent();
    }

    @Override
    public void onModal(ModalAdapter modalAdapter) {
        List<String> values = modalAdapter.getValues();

        values.forEach(value -> play(modalAdapter, of(value), empty(), null));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void play(GenericInteractionAdapter genericInteractionAdapter, Optional<String> identifierOptional, Optional<AttachmentOption> attachmentOptional,
                      Consumer<ModalData> showModalConsumer) {
        if (identifierOptional.isPresent() && attachmentOptional.isPresent()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Некорректное состояние команды \"play\"")
                .setDescription("Обратитесь к администратору для выяснения обстоятельств")));

            genericInteractionAdapter.notify(messageData);
            log.warn("Некорректное состояние команды \"play\"");
            return;
        }

        if (genericInteractionAdapter.isNotCanConnect()) {
            notifyIsNotCanConnect(genericInteractionAdapter);
            return;
        }

        if (identifierOptional.isEmpty() && attachmentOptional.isEmpty()) {
            ModalData modalData = new ModalData()
                .setCustomId("play")
                .setTitle("Добавление в очередь плеера")
                .setComponents(rangeClosed(1, MAX_COMPONENTS)
                    .mapToObj(index -> new ModalComponent()
                        .setTextInputs(List.of(new TextInputComponent()
                            .setId(String.valueOf(index))
                            .setLabel("#" + index)
                            .setStyle(SHORT)
                            .setPlaceholder(STRING_OPTION_DESCRIPTION)
                            .setRequired(index == 1))))
                    .toList());

            showModalConsumer.accept(modalData);
            log.info("Успешно открыто модальное окно для добавления композиций в очередь");
            return;
        }

        final Player player = getPlayer(genericInteractionAdapter.getGuildId());
        int queueSize = player.getQueue().size();
        boolean hasLive = hasLive(player.getQueue());
        Long remained = player.getRemaining();

        Optional<AudioItem> addedOptional = player.add(identifierOptional
            .map(identifier -> new AudioReference(identifier, identifier))
            .or(() -> attachmentOptional
                .map(attachment -> new AudioReference(attachment.getUrl(), attachment.getFileName())))
            .get(), genericInteractionAdapter.getAuthorId());

        if (addedOptional.isEmpty()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Душный запрос")
                .setDescription(NOT_FOUND_DESCRIPTION)));

            genericInteractionAdapter.notify(messageData);
            log.info(NOT_FOUND_DESCRIPTION);
            return;
        }

        player.connect(genericInteractionAdapter);

        AddedInfo added = getAddedInfo(addedOptional.get());
        String description = getDescription(added, queueSize, getRemained(hasLive, remained));

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle(added.getTitle())
            .setDescription(description)
            .setUrl(added.getUrl())
            .setThumbnail(added.getPreview())));

        genericInteractionAdapter.notify(messageData);
        log.info("Композиция успешно добавлена в очередь");
    }

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

            PlayerUtils.getPreview(audioTrack).ifPresent(addedInfo::setPreview);
            return addedInfo;
        } else if (audioItem instanceof AudioPlaylist audioPlaylist) {
            AddedInfo addedInfo = new AddedInfo()
                .setCount(audioPlaylist.getTracks().size())
                .setTitle(audioPlaylist.getName())
                .setDuration(reduceDuration(audioPlaylist.getTracks()))
                .setLive(hasLive(audioPlaylist.getTracks()));

            if (audioPlaylist instanceof YoutubeAudioPlaylist youtubeAudioPlaylist) {
                addedInfo.setUrl("https://www.youtube.com/playlist?list=" + youtubeAudioPlaylist.getPlaylistId());
            }

            audioPlaylist.getTracks().stream().findFirst()
                .flatMap(PlayerUtils::getPreview)
                .ifPresent(addedInfo::setPreview);
            return addedInfo;
        } else {
            throw new IllegalStateException("Unknown audioItem class: " + audioItem.getClass());
        }
    }
}
