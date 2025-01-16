package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.EventCreateCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserOptionOutbound;
import ru.demetrious.deus.bot.app.impl.event.EventComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.text.MessageFormat.format;
import static java.time.Instant.now;
import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_CREATE;
import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_HELP;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.USER;

@RequiredArgsConstructor
@Component
public class EventCreateCommandUseCase implements EventCreateCommandInbound {
    private static final String CRON_OPTION = "cron";
    private static final String USER_OPTION = "user";
    private static final String TITLE_OPTION = "title";
    private static final String DESCRIPTION_OPTION = "description";

    private final GetStringOptionOutbound getStringOptionOutbound;
    private final GetUserOptionOutbound getUserOptionOutbound;
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final EventComponent eventComponent;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(EVENT_CREATE)
            .setDescription("Создать событие календаря")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(TITLE_OPTION)
                    .setDescription("Уникальный заголовок события")
                    .setRequired(true),
                new OptionData()
                    .setType(STRING)
                    .setName(DESCRIPTION_OPTION)
                    .setDescription("Описание событие (будет выведено при уведомлении)")
                    .setRequired(true),
                new OptionData()
                    .setType(STRING)
                    .setName(CRON_OPTION)
                    .setDescription("Дата, время и периодичность события в формате cron (подробнее в команде /%s)".formatted(EVENT_HELP.stringify()))
                    .setRequired(true),
                new OptionData()
                    .setType(USER)
                    .setName(USER_OPTION)
                    .setDescription("Ассоциируемый с событием пользователь")
            ));
    }

    @Override
    public void execute() {
        String title = getStringOptionOutbound.getStringOption(TITLE_OPTION).orElseThrow();
        String description = getStringOptionOutbound.getStringOption(DESCRIPTION_OPTION).orElseThrow();
        String cronExpression = getStringOptionOutbound.getStringOption(CRON_OPTION).orElseThrow();
        Optional<String> userIdOptional = getUserOptionOutbound.getUserIdOption(USER_OPTION);
        Optional<Long> nextFireDateOptional = eventComponent.createEvent(userIdOptional,
            description, title, getGuildIdOutbound.getGuildId(), cronExpression);

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Событие было создано/обновлено")
            .setDescription(format("""
                    Заголовок: {0}
                    Описание: {1}
                    Пользователь: {2}
                    Ближайшее: {3}
                    """,
                title,
                description,
                userIdOptional.map("<@%s>"::formatted).orElse("-"),
                nextFireDateOptional.map("<t:%d>"::formatted).orElse("`Неизвестно`")))
            .setTimestamp(now())));
        notifyOutbound.notify(messageData);
    }
}
