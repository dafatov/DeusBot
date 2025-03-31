package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.demetrious.deus.bot.adapter.duplex.jda.config.AudioSendHandler;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.app.api.channel.GetChannelIdOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.DeferOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.ConnectOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotCanConnectOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.api.thread.CreateThreadOutbound;
import ru.demetrious.deus.bot.app.api.thread.LeaveThreadOutbound;
import ru.demetrious.deus.bot.app.api.user.GetAuthorIdOutbound;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.LINK;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@RequiredArgsConstructor
public abstract class GenericAdapter<A extends Interaction, E extends IReplyCallback, I extends IDeferrableCallback> extends BaseAdapter<E, A>
    implements NotifyOutbound<A>, GetGuildIdOutbound<A>, IsNotConnectedSameChannelOutbound<A>, IsNotCanConnectOutbound<A>, GetAuthorIdOutbound<A>,
    ConnectOutbound<A>, DeferOutbound<A>, CreateThreadOutbound<A>, GetChannelIdOutbound<A>, LeaveThreadOutbound<A> {
    @Autowired
    protected MessageDataMapper messageDataMapper;

    protected static CommandData.Name getName(String[] strings) {
        if (strings.length == 1) {
            return CommandData.Name.from(strings[0], null, null);
        }

        if (strings.length == 2) {
            return CommandData.Name.from(strings[0], null, strings[1]);
        }

        return CommandData.Name.from(strings[0], strings[1], strings[2]);
    }

    protected abstract @NotNull I getInteraction();

    @Override
    public void defer() {
        getEvent().deferReply().queue();
        log.debug("Deferred command's reply");
    }

    @Override
    public void notify(MessageData messageData, boolean isEphemeral) {
        MessageCreateData content = messageDataMapper.mapToMessageCreate(messageData);

        try {
            if (getEvent().isAcknowledged() && isDeferred()) {
                getEvent().getHook()
                    .editOriginal(messageDataMapper.mapToMessageEdit(messageData))
                    .queue();
            } else if (getEvent().isAcknowledged() && !isDeferred()) {
                getEvent().getHook()
                    .sendMessage(content)
                    .setEphemeral(isEphemeral)
                    .queue();
            } else {
                getEvent().reply(content)
                    .setEphemeral(isEphemeral)
                    .queue();
            }
        } catch (Exception e) {
            log.warn("Cannot reply onModal interaction", e);
            ((MessageChannelUnion) requireNonNull(getEvent().getChannel()))
                .sendMessage(content)
                .queue();
        }
    }

    @Override
    public void notify(MessageData messageData) {
        notify(messageData, false);
    }

    @Override
    public Optional<String> createThread(String threadName) {
        if (getEvent().getChannel() instanceof MessageChannelUnion messageChannelUnion && messageChannelUnion instanceof TextChannel textChannel) {
            return textChannel.createThreadChannel(threadName)
                .map(ISnowflake::getId)
                .map(Optional::ofNullable)
                .complete();
        }

        return empty();
    }

    @Override
    public void leaveThread(String id) {
        ofNullable(getEvent().getJDA().getThreadChannelById(id))
            .map(ThreadChannel::leave)
            .ifPresent(RestAction::queue);
    }

    @Override
    public void notifyUnauthorized(Pair<String, URI> authorizeData) {
        MessageData messageData = new MessageData()
            .setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Безавторизационный %s".formatted(authorizeData.getKey()))
                .setDescription("Авторизуйся пройдя по ссылке и повтори команду заново после успешной авторизации и все будет ок")))
            .setComponents(List.of(new MessageComponent().setButtons(List.of(new ButtonComponent()
                .setStyle(LINK)
                .setLabel("Авторизоваться")
                .setId(authorizeData.getValue().toString())))));

        notify(messageData, true);
        log.warn("Произошла попытка запуска неавторизованного запуска команды");
    }

    @Override
    public String getGuildId() {
        return getGuild()
            .map(Guild::getId)
            .orElseThrow();
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

    @Override
    public String getAuthorId() {
        return getEvent().getUser().getId();
    }

    @Override
    public void connectPlayer(AudioSendHandler audioSendHandler) {
        AudioManager audioManager = getAudioManager();

        if (audioManager.isConnected()) {
            return;
        }

        audioManager.setSendingHandler(audioSendHandler);
        audioManager.openAudioConnection(getVoiceChannel());
    }

    @Override
    public String getUserId() {
        return getEvent().getUser().getId();
    }

    @Override
    public Optional<String> getChannelId() {
        return ofNullable(getEvent().getChannel())
            .map(ISnowflake::getId);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private AudioManager getAudioManager() {
        return getAudioManagerOptional()
            .orElseThrow();
    }

    private VoiceChannel getVoiceChannel() {
        return getVoiceChannelOptional()
            .orElseThrow();
    }

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
        return ofNullable(getEvent().getMember())
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
        return ofNullable(getEvent().getGuild());
    }
}
