package ru.demetrious.deus.bot.app.impl.command;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.StatisticVoiceCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.voice.GetGuildVoiceAuditListOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.domain.Audit;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.demetrious.deus.bot.domain.CommandData.Name.STATISTIC_VOICE;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;
import static ru.demetrious.deus.bot.utils.SpellUtils.prettifySeconds;

@RequiredArgsConstructor
@Slf4j
@Component
public class StatisticVoiceCommandUseCase implements StatisticVoiceCommandInbound {
    private final GetGuildVoiceAuditListOutbound getGuildVoiceAuditListOutbound;
    private final List<GetGuildIdOutbound<?>> getGuildIdOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(STATISTIC_VOICE)
            .setDescription("Получить время в голосовых каналах за последнюю неделю");
    }

    @Override
    public void onButton() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        MessageEmbed messageEmbed = getEmbedOutbound.getFirstEmbed();
        List<Audit> guildVoiceAuditList = getGuildVoiceAuditListOutbound.getGuildVoiceAuditList(guildId);
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), guildVoiceAuditList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildVoiceAuditList,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        b(notifyOutbound).notify(messageData);
        log.debug("Список статистики время в голосовых каналах успешно обновлен");
    }

    @Override
    public void execute() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        List<Audit> guildVoiceAuditList = getGuildVoiceAuditListOutbound.getGuildVoiceAuditList(guildId);
        MessageEmbed messageEmbed = new MessageEmbed();
        PaginationComponent paginationComponent = new PaginationComponent(guildVoiceAuditList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildVoiceAuditList,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
        log.info("Список статистики время в голосовых каналах успешно установлен");
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, List<Audit> guildVoiceAuditList, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        double averageSeconds = guildVoiceAuditList.stream()
            .mapToLong(Audit::getCount)
            .average()
            .orElse(0);

        messageEmbed
            .setTitle("Время в голосовых каналах с " + guildVoiceAuditList.stream()
                .min(comparing(Audit::getCreated))
                .map(Audit::getCreated)
                .map(Instant::getEpochSecond)
                .map("<t:%d>"::formatted)
                .orElse("`начала времен`"))
            .setDescription(guildVoiceAuditList.stream()
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .skip(paginationComponent.getStart())
                .map(audit -> mapVoiceAudit(audit, averageSeconds))
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }

    private String mapVoiceAudit(Audit audit, double averageSeconds) {
        return "<@%s>\n%s\n%s".formatted(
            audit.getAuditId().getUserId(),
            prettifySeconds(audit.getCount()),
            averageSeconds == 0 ? EMPTY : mapDeviation(round(100 * (audit.getCount() / averageSeconds - 1)))
        );
    }

    private String mapDeviation(long deviationPercent) {
        if (deviationPercent == 0) {
            return "-# __точно равно__ среднему по больнице";
        }

        return "-# на %d%% __%s__ среднего по больнице".formatted(abs(deviationPercent), deviationPercent > 0 ? "выше" : "ниже");
    }
}
