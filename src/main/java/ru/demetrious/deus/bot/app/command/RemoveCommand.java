package ru.demetrious.deus.bot.app.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.text.MessageFormat.format;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@Slf4j
@Component
public class RemoveCommand extends PlayerCommand {
    public RemoveCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("remove")
            .setDescription("Удаляет композицию из очереди")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName("target")
                    .setDescription("Номер в очереди целевой композиции")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        final Player player = getPlayer(slashCommandAdapter.getGuildId());

        if (slashCommandAdapter.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect(slashCommandAdapter);
            return;
        }

        Optional<Integer> target = slashCommandAdapter.getIntegerOption("target").map(index -> index - 1);
        if (target.map(player::isNotValidIndex).orElse(true)) {
            notifyUnbound(slashCommandAdapter);
            return;
        }

        AudioTrack audioTrack = player.remove(target.orElseThrow());
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Целевая композиция дезинтегрирована")
            .setDescription(format("Композиция **{0}** была стерта из реальности очереди", audioTrack.getInfo().title))));

        slashCommandAdapter.notify(messageData);
        log.info("Композиция была успешно удалена из очереди");
    }
}
