package ru.demetrious.deus.bot.app.impl.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.text.MessageFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.SkipCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.SKIP;

@RequiredArgsConstructor
@Slf4j
@Component
public class SkipCommandUseCase extends PlayerCommand implements SkipCommandInbound {
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(SKIP)
            .setDescription("Пропустить текущую композицию");
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

        AudioTrack audioTrack = player.skip();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Текущая композиция уничтожена")
            .setDescription(MessageFormat.format("Название того, что играло уже не помню. Прошлое должно остаться в прошлом.\n" +
                "...Вроде это **{0}**, но уже какая разница?", audioTrack.getInfo().title))));

        notifyOutbound.notify(messageData);
        log.info("Композиция была успешно пропущена");
    }
}
