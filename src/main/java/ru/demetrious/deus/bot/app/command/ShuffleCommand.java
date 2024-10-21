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
public class ShuffleCommand extends PlayerCommand {
    public ShuffleCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("shuffle")
            .setDescription("Перемешать очередь");
    }

    @Override
    public void execute(SlashCommandAdapter slashCommandAdapter) {
        final Player player = getPlayer(slashCommandAdapter.getGuildId());

        if (slashCommandAdapter.isNotConnectedSameChannel()) {
            notifyIsNotCanConnect(slashCommandAdapter);
            return;
        }

        if (player.isNotValidIndex(0)) {
            notifyUnbound(slashCommandAdapter);
            return;
        }

        player.shuffle();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Плейлист ~~взболтан~~ перемешан")
            .setDescription("""
                Это было суровое время.. Мы мешали песни как могли, чтобы хоть как-то разнообразить свою серую жизнь..
                И  пришел он!! Генератор Псевдо Случайных Чисел или _ГПСЧ_! Он спас нас, но остался в безызвестности.. Так давайте восславим его.
                Присоединяйтесь к _культу ГПСЧ_!!! Да пребудет с Вами **Бог Псевдо Рандома**
                """)));

        slashCommandAdapter.notify(messageData);
        log.info("Плейлист успешно перемешан");
    }
}
