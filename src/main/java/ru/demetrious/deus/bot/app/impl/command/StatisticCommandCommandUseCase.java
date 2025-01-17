package ru.demetrious.deus.bot.app.impl.command;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.GetGuildCommandAuditListOutbound;
import ru.demetrious.deus.bot.app.api.command.StatisticCommandCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.domain.Audit;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.lang.Math.floorDiv;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static ru.demetrious.deus.bot.domain.CommandData.Name.STATISTIC_COMMAND;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Component
public class StatisticCommandCommandUseCase implements StatisticCommandCommandInbound {
    private final GetGuildCommandAuditListOutbound getGuildCommandAuditListOutbound;
    private final List<GetGuildIdOutbound<?>> getGuildIdOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(STATISTIC_COMMAND)
            .setDescription("Получить количество сообщений за последнюю неделю");
    }

    @Override
    public void onButton() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        MessageEmbed messageEmbed = getEmbedOutbound.getEmbed(0);
        Map<CommandData.Name, Long> commandCountMap = getGuildCommandAuditListOutbound.getGuildCommandAuditList(guildId).stream()
            .collect(toMap(audit -> CommandData.Name.valueOf(audit.getAuditId().getName()), Audit::getCount, Long::sum));
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), commandCountMap.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            commandCountMap,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        b(notifyOutbound).notify(messageData);
    }

    @Override
    public void execute() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        List<Audit> guildCommandAuditList = getGuildCommandAuditListOutbound.getGuildCommandAuditList(guildId);
        Map<CommandData.Name, Long> commandCountMap = guildCommandAuditList.stream()
            .collect(toMap(audit -> CommandData.Name.valueOf(audit.getAuditId().getName()), Audit::getCount, Long::sum));
        MessageEmbed messageEmbed = new MessageEmbed()
            .setTitle("Количество выполненных команд с " + guildCommandAuditList.stream()
                .min(comparing(Audit::getCreated))
                .map(Audit::getCreated)
                .map(Instant::toEpochMilli)
                .map(millis -> floorDiv(millis, 1000))
                .map("<t:%d>"::formatted)
                .orElse("`начала времен`"));
        PaginationComponent paginationComponent = new PaginationComponent(commandCountMap.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            commandCountMap,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, Map<CommandData.Name, Long> commandCountMap, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        messageEmbed
            .setDescription(commandCountMap.entrySet().stream()
                .skip(paginationComponent.getStart())
                .map(this::mapCommandAudit)
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }

    private String mapCommandAudit(Map.Entry<CommandData.Name, Long> commandCountEntry) {
        return "%s\n%d".formatted(
            commandCountEntry.getKey().stringify(),
            commandCountEntry.getValue()
        );
    }
}
