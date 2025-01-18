package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.EventShowCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.app.impl.event.EventComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.lang.Math.floorDiv;
import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.DESCRIPTION;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.USER_ID;
import static ru.demetrious.deus.bot.domain.CommandData.Name.EVENT_SHOW;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Component
public class EventShowCommandUseCase implements EventShowCommandInbound {
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
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
        List<Trigger> triggerList = eventComponent.getTriggerList();
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), triggerList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            triggerList,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        b(notifyOutbound).notify(messageData);
    }

    @SneakyThrows
    @Override
    public void execute() {
        List<Trigger> triggerList = eventComponent.getTriggerList();
        MessageEmbed messageEmbed = new MessageEmbed()
            .setTitle("Запущенные события");
        PaginationComponent paginationComponent = new PaginationComponent(triggerList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            triggerList,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, List<Trigger> triggerList, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        messageEmbed
            .setDescription(triggerList.stream()
                .skip(paginationComponent.getStart())
                .map(this::mapEvent)
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }

    private String mapEvent(Trigger trigger) {
        return format("""
                {0}
                -# Описание: {1}
                -# Пользователь: {2}
                -# Начат: <t:{3}>
                -# Следующее: <t:{4}>
                -# Периодичность: {5}
                """,
            trigger.getKey().getName(),
            trigger.getJobDataMap().get(DESCRIPTION),
            ofNullable(trigger.getJobDataMap().get(USER_ID))
                .map("<@%s>"::formatted)
                .orElse("-"),
            Long.toString(floorDiv(trigger.getStartTime().getTime(), 1000)),
            Long.toString(floorDiv(trigger.getNextFireTime().getTime(), 1000)),
            Optional.of(trigger)
                .filter(t -> t instanceof CronTrigger)
                .map(CronTrigger.class::cast)
                .map(CronTrigger::getCronExpression)
                .orElse("`Неизвестно`")
        );
    }
}
