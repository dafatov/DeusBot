package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.EventHelpCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_HELP;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventHelpCommandUseCase implements EventHelpCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(EVENT_HELP)
            .setDescription("Помощь по командам текущей группы");
    }

    @Override
    public void execute() {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Помощь немощным")
            .setDescription("Для создания и понимания cron выражений рекомендуется использовать " +
                "[ресурс](https://www.freeformatter.com/cron-expression-generator-quartz.html)")));

        notifyOutbound.notify(messageData);
        log.info("Помощь по событиям успешно выведена");
    }
}
