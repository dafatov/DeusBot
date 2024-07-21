package ru.demetrious.deus.bot.adapter.inbound.jda;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
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
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;

@Slf4j
@RequiredArgsConstructor
public class CommandAdapterImpl implements CommandAdapter {
    private final MessageDataMapper messageDataMapper;
    private final SlashCommandInteractionEvent event;
    private final Player player;

    @Override
    public void notify(String content) {
        notify(new MessageData().setContent(content));
    }

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
    public void connectPlayer() {
        VoiceChannel voiceChannel = ofNullable(event.getMember())
            .map(Member::getVoiceState)
            .map(GuildVoiceState::getChannel)
            .map(AudioChannelUnion::asVoiceChannel)
            .orElseThrow();
        AudioManager audioManager = ofNullable(event.getGuild())
            .map(Guild::getAudioManager)
            .orElseThrow();

        audioManager.setSendingHandler(player.getAudioSendHandler());
        audioManager.openAudioConnection(voiceChannel);

        player.add("https://hls-01-radiorecord.hostingradio.ru/record-progr/playlist.m3u8");
    }

    @Override
    public List<AudioTrack> getQueue() {
        return player.getQueue();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Boolean isDeferred() throws InterruptedException, ExecutionException {
        return event.getInteraction().getHook().retrieveOriginal().submit()
            .thenApply(Message::getFlags)
            .thenApply(messageFlags -> messageFlags.contains(LOADING))
            .get();
    }
}
