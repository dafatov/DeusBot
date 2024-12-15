package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.PauseCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.PAUSE;

@RequiredArgsConstructor
@Slf4j
@Component
public class PauseCommandUseCase extends PlayerCommand implements PauseCommandInbound {
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(PAUSE)
            .setDescription("Приостановить/возобновить проигрывание композиции");
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

        notifyOutbound.notify(messageData);
        log.info("Композиция была успешна " + (isPause ? "приостановлена" : "возобновлена"));
    }
}
