package ru.demetrious.deus.bot.app.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

@Slf4j
@Component
public class SkipCommand extends PlayerCommand {
    public SkipCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("skip")
            .setDescription("Пропустить текущую композицию");
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        final Player player = getPlayer(slashCommandAdapter.getGuildId());

        if (player.isNotPlaying()) {
            notifyIsNotPlaying(slashCommandAdapter);
            return;
        }

        if (slashCommandAdapter.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect(slashCommandAdapter);
            return;
        }

        AudioTrack audioTrack = player.skip();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Текущая композиция уничтожена")
            .setDescription(MessageFormat.format("Название того, что играло уже не помню. Прошлое должно остаться в прошлом.\n" +
                "...Вроде это **{0}**, но уже какая разница?", audioTrack.getInfo().title))));

        slashCommandAdapter.notify(messageData);
        log.info("Композиция была успешно пропущена");
    }
}
