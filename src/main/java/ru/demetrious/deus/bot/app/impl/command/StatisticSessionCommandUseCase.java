package ru.demetrious.deus.bot.app.impl.command;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.command.StatisticSessionCommandInbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.session.GetGuildSessionListOutbound;
import ru.demetrious.deus.bot.app.impl.component.PaginationComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.Session;

import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.demetrious.deus.bot.domain.CommandData.Name.STATISTIC_SESSION;
import static ru.demetrious.deus.bot.domain.Session.State.UNRELIABLE_FINISH;
import static ru.demetrious.deus.bot.domain.Session.State.UNRELIABLE_START;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;
import static ru.demetrious.deus.bot.utils.SpellUtils.prettifySeconds;

@RequiredArgsConstructor
@Slf4j
@Component
public class StatisticSessionCommandUseCase implements StatisticSessionCommandInbound {
    private static final String APPROXIMATELY = " *примерно*";
    private static final Comparator<Session> SESSION_COMPARATOR = (a, b) -> {
        if (nonNull(a.getFinish()) && nonNull(b.getFinish())) {
            return b.getFinish().compareTo(a.getFinish());
        }

        if (nonNull(a.getFinish())) {
            return 1;
        }

        if (nonNull(b.getFinish())) {
            return -1;
        }

        return b.getStart().compareTo(a.getStart());
    };

    private final GetGuildSessionListOutbound getGuildSessionListOutbound;
    private final List<GetGuildIdOutbound<?>> getGuildIdOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetEmbedOutbound getEmbedOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(STATISTIC_SESSION)
            .setDescription("Получить последние голосовые сессии на сервере");
    }

    @Override
    public void onButton() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        MessageEmbed messageEmbed = getEmbedOutbound.getFirstEmbed();
        List<Session> guildSessionList = getGuildSessionListOutbound.getGuildSessionList(guildId);
        PaginationComponent paginationComponent = PaginationComponent.from(messageEmbed.getFooter(), guildSessionList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildSessionList,
            paginationComponent,
            paginationComponent.update(getCustomIdOutbound.getCustomId())
        );

        b(notifyOutbound).notify(messageData);
        log.debug("Список статистики сессии успешно обновлен");
    }

    @Override
    public void execute() {
        String guildId = b(getGuildIdOutbound).getGuildId();
        MessageEmbed messageEmbed = new MessageEmbed()
            .setTitle("Последние голосовые сессии");
        List<Session> guildSessionList = getGuildSessionListOutbound.getGuildSessionList(guildId);
        PaginationComponent paginationComponent = new PaginationComponent(guildSessionList.size());

        MessageData messageData = updateMessage(
            messageEmbed,
            guildSessionList,
            paginationComponent,
            paginationComponent.get()
        );

        b(notifyOutbound).notify(messageData);
        log.info("Список статистики сессии успешно установлен");
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private MessageData updateMessage(MessageEmbed messageEmbed, List<Session> guildSessionList, PaginationComponent paginationComponent,
                                      MessageComponent paginationMessageComponent) {
        messageEmbed
            .setDescription(guildSessionList.stream()
                .sorted(SESSION_COMPARATOR)
                .skip(paginationComponent.getStart())
                .map(this::mapSession)
                .limit(paginationComponent.getCount())
                .collect(joining("\n\n")))
            .setFooter(paginationComponent.getFooter());

        return new MessageData()
            .setEmbeds(List.of(messageEmbed))
            .setComponents(List.of(paginationMessageComponent));
    }

    private String mapSession(Session session) {
        return "<@%s>\n<t:%d:R>%s\n%s".formatted(
            session.getId().getUserId(),
            session.getStart().getEpochSecond(),
            session.inState(UNRELIABLE_START) ? APPROXIMATELY : EMPTY,
            ofNullable(session.getFinish())
                .map(finish -> mapSessionFinish(session, finish))
                .orElse("`Сейчас`")
        );
    }

    private String mapSessionFinish(Session session, Instant finish) {
        return "<t:%d:R>%s\n%s%s".formatted(
            finish.getEpochSecond(),
            session.inState(UNRELIABLE_FINISH) ? APPROXIMATELY : EMPTY,
            prettifySeconds(between(session.getStart(), finish).getSeconds()),
            session.inState(UNRELIABLE_START) || session.inState(UNRELIABLE_FINISH) ? APPROXIMATELY : EMPTY
        );
    }
}
