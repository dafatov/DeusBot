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
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;

@Slf4j
@RequiredArgsConstructor
public abstract class GenericInteractionAdapterImpl<Event extends GenericInteractionCreateEvent & IDeferrableCallback & IReplyCallback, Interaction extends IDeferrableCallback>
    implements GenericInteractionAdapter<Interaction> {
    protected final Event event;
    private final MessageDataMapper messageDataMapper;

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
            log.warn("Cannot reply onModal interaction", e);
            ((MessageChannelUnion) requireNonNull(event.getChannel())).sendMessage(content).queue();
        }
    }

    @Override
    public AudioManager getAudioManager() {
        return getAudioManagerOptional()
            .orElseThrow();
    }

    @Override
    public VoiceChannel getVoiceChannel() {
        return getVoiceChannelOptional()
            .orElseThrow();
    }

    @Override
    public String getGuildId() {
        return getGuild()
            .map(Guild::getId)
            .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Interaction getInteraction() {
        return (Interaction) event.getInteraction();
    }

    @Override
    public boolean isNotConnectedSameChannel() {
        return getVoiceChannelOptional().isEmpty()
            || !getAudioManager().isConnected() || isNotSameChannels();
    }

    @Override
    public boolean isNotCanConnect() {
        return getVoiceChannelOptional().isEmpty()
            || getAudioManager().isConnected() && isNotSameChannels();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Optional<AudioManager> getAudioManagerOptional() {
        return getGuild()
            .map(Guild::getAudioManager);
    }

    private boolean isNotSameChannels() {
        return !getAudioManagerOptional()
            .map(AudioManager::getConnectedChannel)
            .map(AudioChannelUnion::asVoiceChannel)
            .equals(getVoiceChannelOptional());
    }

    private Optional<VoiceChannel> getVoiceChannelOptional() {
        return ofNullable(event.getMember())
            .map(Member::getVoiceState)
            .map(GuildVoiceState::getChannel)
            .map(AudioChannelUnion::asVoiceChannel);
    }

    private Boolean isDeferred() throws InterruptedException, ExecutionException {
        return getInteraction().getHook().retrieveOriginal().submit()
            .thenApply(Message::getFlags)
            .thenApply(messageFlags -> messageFlags.contains(LOADING))
            .get();
    }

    private Optional<Guild> getGuild() {
        return ofNullable(event.getGuild());
    }
}
