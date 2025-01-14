package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.EventShowCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.message.UpdateMessageOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.app.impl.event.EventComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.util.stream.Collectors.joining;
import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_SHOW;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Component
public class EventShowCommandUseCase implements EventShowCommandInbound {
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final UpdateMessageOutbound updateMessageOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;
    private final EventComponent eventComponent;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(EVENT_SHOW)
            .setDescription("Показывает ранее созданные события");
    }

    @SneakyThrows
    @Override
    public void onButton() {
        MessageEmbed messageEmbed = getEmbedOutbound.getEmbed(0);
        List<String> jobDetailList = eventComponent.getEventKeyList();
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), jobDetailList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            jobDetailList,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        updateMessageOutbound.update(messageData);
    }

    @SneakyThrows
    @Override
    public void execute() {
        List<String> jobDetailList = eventComponent.getEventKeyList();
        MessageEmbed messageEmbed = new MessageEmbed()
            .setTitle("Запущенные события");
        PaginationComponent paginationComponent = new PaginationComponent(jobDetailList.size());
        MessageData messageData = updateMessage(
            messageEmbed,
            jobDetailList,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, List<String> jobKeySet, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        messageEmbed
            .setDescription(jobKeySet.stream()
                .skip(paginationComponent.getStart())
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }
}
