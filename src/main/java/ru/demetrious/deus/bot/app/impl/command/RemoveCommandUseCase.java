package ru.demetrious.deus.bot.app.impl.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.RemoveCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.text.MessageFormat.format;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REMOVE;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@RequiredArgsConstructor
@Slf4j
@Component
public class RemoveCommandUseCase extends PlayerCommand implements RemoveCommandInbound {
    private static final String TARGET = "target";

    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetIntegerOptionOutbound getIntegerOptionOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REMOVE)
            .setDescription("Удаляет композицию из очереди")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET)
                    .setDescription("Номер в очереди целевой композиции")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        final Player player = getPlayer(getGuildIdOutbound.getGuildId());

        if (isNotConnectedSameChannelOutbound.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect();
            return;
        }

        Optional<Integer> target = getIntegerOptionOutbound.getIntegerOption(TARGET).map(index -> index - 1);
        if (target.map(player::isNotValidIndex).orElse(true)) {
            notifyUnbound();
            return;
        }

        AudioTrack audioTrack = player.remove(target.orElseThrow());
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Целевая композиция дезинтегрирована")
            .setDescription(format("Композиция **{0}** была стерта из реальности очереди", audioTrack.getInfo().title))));

        notifyOutbound.notify(messageData);
        log.info("Композиция была успешно удалена из очереди");
    }
}
