package ru.demetrious.deus.bot.app.impl.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.MoveCommandInbound;
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
import static java.util.stream.Stream.of;
import static ru.demetrious.deus.bot.domain.CommandData.Name.MOVE;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@RequiredArgsConstructor
@Slf4j
@Component
public class MoveCommandUseCase extends PlayerCommand implements MoveCommandInbound {
    protected static final String TARGET = "target";
    protected static final String TARGET_DESCRIPTION = "Номер в очереди целевой композиции";
    private static final String POSITION = "position";

    private final GetIntegerOptionOutbound getIntegerOptionOutbound;
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(MOVE)
            .setDescription("Переместить композицию с места в очереди на другое")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET)
                    .setDescription(TARGET_DESCRIPTION)
                    .setRequired(true),
                new OptionData()
                    .setType(INTEGER)
                    .setName(POSITION)
                    .setDescription("Номер конечной позиции целевой композиции")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        move(getIntegerOptionOutbound.getIntegerOption(POSITION).map(index -> index - 1));
    }

    protected void move(Optional<Integer> position) {
        final Player player = getPlayer(getGuildIdOutbound.getGuildId());
        Optional<Integer> target = getIntegerOptionOutbound.getIntegerOption(TARGET).map(index -> index - 1);

        if (isNotConnectedSameChannelOutbound.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect();
            return;
        }

        if (of(target, position).flatMap(Optional::stream).distinct().filter(player::isValidIndex).count() < 2) {
            notifyUnbound();
            return;
        }

        AudioTrack audioTrack = player.move(target.orElseThrow(), position.orElseThrow());
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Целевая композиция передвинута")
            .setDescription(format("Композиция **{0}** протолкала всех локтями на позицию **{1}**.\nКто бы сомневался. Донатеры \\*\\*\\*\\*ые",
                audioTrack.getInfo().title,
                position.map(index -> index + 1).orElseThrow()))));

        notifyOutbound.notify(messageData);
        log.info("Композиция была успешна перемещена");
    }
}
