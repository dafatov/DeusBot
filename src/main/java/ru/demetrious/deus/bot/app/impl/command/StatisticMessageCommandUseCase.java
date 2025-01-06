package ru.demetrious.deus.bot.app.impl.command;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.StatisticMessageCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.message.GetGuildMessageAuditListOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.message.UpdateMessageOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.domain.Audit;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.lang.Math.floorDiv;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static ru.demetrious.deus.bot.domain.CommandData.Name.STATISTIC_MESSAGE;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Component
public class StatisticMessageCommandUseCase implements StatisticMessageCommandInbound {
    private final GetGuildMessageAuditListOutbound getGuildMessageAuditListOutbound;
    private final List<GetGuildIdOutbound<?>> getGuildIdOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final UpdateMessageOutbound updateMessageOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(STATISTIC_MESSAGE)
            .setDescription("Получить количество сообщений за последнюю неделю");
    }

    @Override
    public void onButton() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        MessageEmbed messageEmbed = getEmbedOutbound.getEmbed(0);
        List<Audit> guildMessageAuditList = getGuildMessageAuditListOutbound.getGuildMessageAuditList(guildId);
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), guildMessageAuditList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildMessageAuditList,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        updateMessageOutbound.update(messageData);
    }

    @Override
    public void execute() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        List<Audit> guildMessageAuditList = getGuildMessageAuditListOutbound.getGuildMessageAuditList(guildId);
        MessageEmbed messageEmbed = new MessageEmbed()
            .setTitle("Количество сообщений с " + guildMessageAuditList.stream()
                .min(comparing(Audit::getCreated))
                .map(Audit::getCreated)
                .map(Instant::toEpochMilli)
                .map(millis -> floorDiv(millis, 1000))
                .map("<t:%d>"::formatted)
                .orElse("`начала времен`"));
        PaginationComponent paginationComponent = new PaginationComponent(guildMessageAuditList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildMessageAuditList,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, List<Audit> guildMessageAuditList, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        messageEmbed
            .setDescription(guildMessageAuditList.stream()
                .skip(paginationComponent.getStart())
                .map(this::mapMessageAudit)
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }

    private String mapMessageAudit(Audit audit) {
        return "<@%s>\n%d".formatted(
            audit.getAuditId().getUserId(),
            audit.getCount()
        );
    }
}
