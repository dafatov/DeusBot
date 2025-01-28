package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.PingCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.network.GetLatencyOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static ru.demetrious.deus.bot.domain.CommandData.Name.PING;

@RequiredArgsConstructor
@Slf4j
@Component
public class PingCommandUseCase implements PingCommandInbound {
    private final GetLatencyOutbound getLatencyOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(PING)
            .setDescription("Пинг туды-сюды");
    }

    @Override
    public void execute() {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Мое время обработки данных")
            .setDescription(format("Решал на досуге задачи тысячелетия и решил за {0}мс. Их все.", getLatencyOutbound.getLatency()))));

        notifyOutbound.notify(messageData);
        log.info("Пинг успешно выведен");
    }
}
