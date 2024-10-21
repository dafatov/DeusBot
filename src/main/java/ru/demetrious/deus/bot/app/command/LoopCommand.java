package ru.demetrious.deus.bot.app.command;

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
public class LoopCommand extends PlayerCommand {
    public LoopCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("loop")
            .setDescription("Зациклить/отциклить проигрывание композиции");
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

        if (player.isPlayingLive()) {
            notifyIsLive(slashCommandAdapter);
            return;
        }

        boolean isLoop = player.loop();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Проигрывание " + (isLoop ? "зациклена" : "отциклена"))
            .setDescription(isLoop ? "オラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオ" +
                "ラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラ..."
                : "しーん...")));

        slashCommandAdapter.notify(messageData);
        log.info("Композиция была успешна " + (isLoop ? "зациклена" : "отциклена"));
    }
}
