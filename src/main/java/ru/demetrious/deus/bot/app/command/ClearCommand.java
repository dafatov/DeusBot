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

import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@Component
public class ClearCommand extends PlayerCommand {
    public ClearCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("clear")
            .setDescription("Очистить очередь");
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        Player player = getPlayer(slashCommandAdapter.getGuildId());

        if (player.getQueue().isEmpty()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Мир музыки пуст")
                .setDescription("Может ли существовать мир без музыки? Каким бы он был...\nАх да! Таким, в котором сейчас живешь ты~~")));

            slashCommandAdapter.notify(messageData);
            log.warn("Не выполнена команда. Очередь пуста");
            return;
        }

        if (slashCommandAdapter.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect(slashCommandAdapter);
            return;
        }

        player.clear();

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Э-эм. а где все?")
            .setDescription("Ох.. Эти времена, эти нравы.. Кто-то созидает, а кто-то может только уничтожать.\n" +
                "Поздравляю разрушитель, у тебя получилось. **Плейлист очищен**")));

        slashCommandAdapter.notify(messageData);
        log.info("Плейлист успешно очищен");
    }
}
