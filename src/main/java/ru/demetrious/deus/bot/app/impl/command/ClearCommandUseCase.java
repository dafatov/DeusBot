package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.ClearCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.CLEAR;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClearCommandUseCase extends PlayerCommand implements ClearCommandInbound {
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(CLEAR)
            .setDescription("Очистить очередь");
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

        player.clear();

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Э-эм. а где все?")
            .setDescription("Ох.. Эти времена, эти нравы.. Кто-то созидает, а кто-то может только уничтожать.\n" +
                "Поздравляю разрушитель, у тебя получилось. **Плейлист очищен**")));

        notifyOutbound.notify(messageData);
        log.info("Плейлист успешно очищен");
    }
}
