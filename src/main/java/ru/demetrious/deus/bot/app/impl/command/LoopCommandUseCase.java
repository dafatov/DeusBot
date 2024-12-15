package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.LoopCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.LOOP;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoopCommandUseCase extends PlayerCommand implements LoopCommandInbound {
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(LOOP)
            .setDescription("Зациклить/отциклить проигрывание композиции");
    }

    @Override
    public void execute() {
        final Player player = getPlayer(getGuildIdOutbound.getGuildId());

        if (player.isNotPlaying()) {
            notifyIsNotPlaying();
            return;
        }

        if (isNotConnectedSameChannelOutbound.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect();
            return;
        }

        if (player.isPlayingLive()) {
            notifyIsLive();
            return;
        }

        boolean isLoop = player.loop();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Проигрывание " + (isLoop ? "зациклена" : "отциклена"))
            .setDescription(isLoop ? "オラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオ" +
                "ラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラ..."
                : "しーん...")));

        notifyOutbound.notify(messageData);
        log.info("Композиция была успешна " + (isLoop ? "зациклена" : "отциклена"));
    }
}
