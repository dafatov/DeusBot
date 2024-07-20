package ru.demetrious.deus.bot.adapter.inbound.jda;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.domain.MessageData;

import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;

@Slf4j
@RequiredArgsConstructor
public class CommandAdapterImpl implements CommandAdapter {
    private final MessageDataMapper messageDataMapper;
    private final SlashCommandInteractionEvent event;

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
        VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);

        DefaultAudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        AudioPlayer player = audioPlayerManager.createPlayer();

        TrackScheduler trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));

        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());

        audioPlayerManager.loadItem("https://hls-01-radiorecord.hostingradio.ru/record-progr/playlist.m3u8", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                trackScheduler.queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
                    trackScheduler.queue(audioTrack);
                }
            }

            @Override
            public void noMatches() {
                log.warn("Load no matches");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                log.error("Load failed", e);
            }
        });
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
