package ru.demetrious.deus.bot.app.command;

import java.awt.Color;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static java.time.Instant.now;

@Slf4j
@Component
public class PingCommand implements Command {
    @Override
    public CommandData getData() {
        return new CommandData()
            .setName("ping")
            .setDescription("Пинг туды-сюды");
    }

    @Override
    public void execute(CommandAdapter commandAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(new Color(255, 255, 80))
            .setTitle("Мое время обработки данных")
            .setDescription(format("Решал на досуге задачи тысячелетия и решил за {0}мс. Их все.", commandAdapter.getLatency()))
            .setTimestamp(now())));

        commandAdapter.notify(messageData);
    }
}
