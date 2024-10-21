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
public class PauseCommand extends PlayerCommand {
    public PauseCommand(Jukebox jukebox) {
        super(jukebox);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("pause")
            .setDescription("Приостановить/возобновить проигрывание композиции");
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

        boolean isPause = player.pause();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Проигрывание " + (isPause ? "приостановлено" : "возобновлено"))
            .setDescription(isPause ? """
                -- Однажды, давным давно, когда я еще был молодым, мне повстречался человек необычайных талантов. Я тогда не мог даже представить, что человеческий мозг в состоянии на такое...
                -- Что же он мог, деда?
                -- Ох, молодежь пошла, не перебивай старших, если хочешь услышать продолжение...
                -- Извини, деда
                -- Ну, так вот, на чем я остановился? Ах, да! Я встретил человека с крайне необычным разумом. До сих пор, смотря сквозь призму лет, я все еще с трудом верю, что такое могло произойти. Ну так вот, этот человек....
                -- ...
                -- ...
                -- Деда, что с тобой? Все в порядке? Ты чего завис???
                """ : """
                -- Деда, что с тобой? Все в порядке? Ты чего завис???
                -- Да в порядке я. Уснул чутка.
                -- Слава богу
                -- Заинтриговал? Хочешь услышать продолжение истории?
                -- Да, деда. Хочу. Очень хочу
                -- Так вот. давным давно встреченный человек с необычайным разумом...
                -- Деда! Не тяни!
                -- Хорошо, внучок, хорошо. Так вот тот человек ||установил доту|| и ||пошел в рейтинг в соло с рандомами||
                -- Боже.. и что с ним стало после?
                -- Да ничего особенного. ||Апнул 5К ММР||
                -- Ничего себе, деда.
                -- Да, внучок. Теперь он в лучшем мире. Еще пару лет и я тоже туда отправлюсь
                -- Не говори такое, деда.. Такого даже врагу не пожелаешь
                -- Ха-ха-ха... Все будет в порядке внучок. Это естественно.
                """)));

        slashCommandAdapter.notify(messageData);
        log.info("Композиция была успешна " + (isPause ? "приостановлена" : "возобновлена"));
    }
}
