package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.command.EventRemoveCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.impl.event.EventComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.time.Instant.now;
import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_REMOVE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.INFO;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventRemoveCommandUseCase implements EventRemoveCommandInbound {
    private static final String TITLE_OPTION = "title";

    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final EventComponent eventComponent;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(EVENT_REMOVE)
            .setDescription("Удаляет ранее созданные события")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(TITLE_OPTION)
                    .setDescription("Уникальный заголовок события")
                    .setRequired(true)
                    .setAutoComplete(true)
            ));
    }

    @Override
    public void onAutocomplete() {
        List<OptionChoice> optionChoiceList = eventComponent.getEventKeyList().stream()
            .filter(title -> title.startsWith(getFocusedOptionOutbound.getFocusedOption().getValue()))
            .map(title -> new OptionChoice()
                .setName(title)
                .setValue(title))
            .toList();

        replyChoicesOutbound.replyChoices(optionChoiceList);
    }

    @SneakyThrows
    @Override
    public void execute() {
        String title = getStringOptionOutbound.getStringOption(TITLE_OPTION).orElseThrow();
        boolean isRemoved = eventComponent.removeEvent(title);

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Событие \"%s\" %s".formatted(title, isRemoved ? "было удалено" : "не найдено"))
            .setColor(isRemoved ? INFO : WARNING)
            .setTimestamp(now())));
        notifyOutbound.notify(messageData);
        log.info("Событие успешно удалено");
    }
}
