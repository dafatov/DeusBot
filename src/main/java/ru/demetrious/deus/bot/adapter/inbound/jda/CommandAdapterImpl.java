package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.entities.Message.Attachment;
import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;

@Slf4j
@RequiredArgsConstructor
public class CommandAdapterImpl implements CommandAdapter {
    private final MessageDataMapper messageDataMapper;
    private final SlashCommandInteractionEvent event;

    @Override
    public void notify(MessageData messageData) {
        MessageCreateData content = messageDataMapper.mapToMessageCreate(messageData);

        try {
            if (event.isAcknowledged() && isDeferred()) {
                event.getHook().editOriginal(messageDataMapper.mapToMessageEdit(messageData)).queue();
            } else if (event.isAcknowledged() && !isDeferred()) {
                event.getHook().sendMessage(content).queue();
            } else {
                event.reply(content).queue();
            }
        } catch (Exception e) {
            log.warn("Cannot reply command interaction", e);
            event.getChannel().sendMessage(content).queue();
        }
    }

    @Override
    public String getLatency() {
        return "?";
    }

    @Override
    public VoiceChannel getVoiceChannel() {
        return getVoiceChannelOptional()
            .orElseThrow();
    }

    @Override
    public AudioManager getAudioManager() {
        return getGuild()
            .map(Guild::getAudioManager)
            .orElseThrow();
    }

    @Override
    public String getGuildId() {
        return getGuild()
            .map(Guild::getId)
            .orElseThrow();
    }

    @Override
    public Optional<String> getStringOption(String name) {
        return ofNullable(event.getOption(name))
            .map(OptionMapping::getAsString);
    }

    @Override
    public Optional<Attachment> getAttachmentOption(String name) {
        return ofNullable(event.getOption(name))
            .map(OptionMapping::getAsAttachment);
    }

    @Override
    public boolean isUnequalChannels() {
        return getVoiceChannelOptional().isEmpty() ||
            getAudioManager().isConnected() && !getVoiceChannel().equals(requireNonNull(getAudioManager().getConnectedChannel()).asVoiceChannel());
    }

    @Override
    public void showModal(Modal modal) {
        event.replyModal(modal).queue();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    @NotNull
    private Optional<VoiceChannel> getVoiceChannelOptional() {
        return ofNullable(event.getMember())
            .map(Member::getVoiceState)
            .map(GuildVoiceState::getChannel)
            .map(AudioChannelUnion::asVoiceChannel);
    }

    private Boolean isDeferred() throws InterruptedException, ExecutionException {
        return event.getInteraction().getHook().retrieveOriginal().submit()
            .thenApply(Message::getFlags)
            .thenApply(messageFlags -> messageFlags.contains(LOADING))
            .get();
    }

    private Optional<Guild> getGuild() {
        return ofNullable(event.getGuild());
    }
}
