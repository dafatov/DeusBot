package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.ClearCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.CLEAR;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClearCommandUseCase extends PlayerCommand implements ClearCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(CLEAR)
            .setDescription("Очистить очередь");
    }

    @Override
    public void execute() {
        Result.Status status = getPlayer().clear().getStatus();

        switch (status) {
            case IS_NOT_PLAYING -> notifyIsNotPlaying();
            case NOT_SAME_CHANNEL -> notifyIsNotCanConnect();
            case OK -> {
                MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                    .setTitle("Э-эм. а где все?")
                    .setDescription("Ох.. Эти времена, эти нравы.. Кто-то созидает, а кто-то может только уничтожать.\n" +
                        "Поздравляю разрушитель, у тебя получилось. **Плейлист очищен**")));

                notifyOutbound.notify(messageData);
                log.info("Плейлист успешно очищен");
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(status));
        }
    }
}
