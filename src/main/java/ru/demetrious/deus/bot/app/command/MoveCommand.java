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
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@Slf4j
@Component
public class MoveCommand extends PlayerCommand {
    protected static final String TARGET_DESCRIPTION = "Номер в очереди целевой композиции";

    public MoveCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("move")
            .setDescription("Переместить композицию с места в очереди на другое")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName("target")
                    .setDescription(TARGET_DESCRIPTION)
                    .setRequired(true),
                new OptionData()
                    .setType(INTEGER)
                    .setName("position")
                    .setDescription("Номер конечной позиции целевой композиции")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        move(slashCommandAdapter, slashCommandAdapter.getIntegerOption("target").map(index -> index - 1),
            slashCommandAdapter.getIntegerOption("position").map(index -> index - 1));
    }

    public void move(SlashCommandAdapter slashCommandAdapter, Optional<Integer> target, Optional<Integer> position) {
        final Player player = getPlayer(slashCommandAdapter.getGuildId());

        if (slashCommandAdapter.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect(slashCommandAdapter);
            return;
        }

        if (target.filter(player::isValidIndex).equals(position.filter(player::isValidIndex))) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Ты это.. Вселенной ошибся, чел.")
                .setDescription(format(
                    "Типа знаешь вселенная расширяется, а твой мозг походу нет. Ну вышел ты за пределы размеров очереди или решил написать одинаковые индексы.\n" +
                        "Диапазон значений _от 1 до {0}_",
                    player.getQueue().size()))));

            slashCommandAdapter.notify(messageData);
            log.info("Выход за пределы очереди");
            return;
        }

        AudioTrack audioTrack = player.move(target.orElseThrow(), position.orElseThrow());
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Целевая композиция передвинута")
            .setDescription(format("Композиция **{0}** протолкала всех локтями на позицию **{1}**.\nКто бы сомневался. Донатеры \\*\\*\\*\\*ые",
                audioTrack.getInfo().title,
                position.map(index -> index + 1).orElseThrow()))));

        slashCommandAdapter.notify(messageData);
        log.info("Композиция была успешна перемещена");
    }
}
