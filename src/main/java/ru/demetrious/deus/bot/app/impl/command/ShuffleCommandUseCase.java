package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.ShuffleCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.SHUFFLE;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShuffleCommandUseCase extends PlayerCommand implements ShuffleCommandInbound {
    private final IsNotConnectedSameChannelOutbound<SlashCommandInteractionInbound> isNotConnectedSameChannelOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(SHUFFLE)
            .setDescription("Перемешать очередь");
    }

    @Override
    public void execute() {
        Result.Status status = getPlayer().shuffle().getStatus();

        switch (status) {
            case NOT_SAME_CHANNEL -> notifyIsNotCanConnect();
            case UNBOUND -> notifyUnbound();
            case OK -> {
                MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                    .setTitle("Плейлист ~~взболтан~~ перемешан")
                    .setDescription("""
                        Это было суровое время.. Мы мешали песни как могли, чтобы хоть как-то разнообразить свою серую жизнь..
                        И  пришел он!! Генератор Псевдо Случайных Чисел или _ГПСЧ_! Он спас нас, но остался в безызвестности.. Так давайте восславим его.
                        Присоединяйтесь к _культу ГПСЧ_!!! Да пребудет с Вами **Бог Псевдо Рандома**
                        """)));

                notifyOutbound.notify(messageData);
                log.info("Плейлист успешно перемешан");
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(status));
        }
    }
}
