package ru.demetrious.deus.bot.app.impl.publication;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.freebie.GetFreebieListOutbound;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.freebie.FreebieItem;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

@InitScheduled(name = "freebie", groupName = "publication", cron = "0 0 0/1 ? * *")
@Slf4j
@RequiredArgsConstructor
@Component
public class FreebiePublicationJob extends PublicationJob {
    private final GetFreebieListOutbound freebieListOutbound;

    @Override
    Map<Optional<String>, MessageData> supplyMessage(@NotNull JobExecutionContext context) {
        return Optional.of(freebieListOutbound.getFreebieList().stream()
                .filter(freebieItem -> ofNullable(context.getPreviousFireTime())
                    .map(Date::toInstant)
                    .map(instant -> freebieItem.getPubDate().isAfter(instant))
                    .orElse(true))
                .map(freebieItem -> new MessageEmbed()
                    .setUrl(freebieItem.getLink())
                    .setTitle(freebieItem.getTitle())
                    .setDescription(freebieItem.getDescription())
                    .setThumbnail(ofNullable(freebieItem.getCategory())
                        .map(FreebieItem.Category::getThumbnail)
                        .orElse("https://static10.tgstat.ru/channels/_0/0c/0c75b8bf567806a342839cb1a406f4f8.jpg"))
                    .setTimestamp(freebieItem.getPubDate()))
                .sorted(comparing(MessageEmbed::getTimestamp))
                .toList())
            .filter(CollectionUtils::isNotEmpty)
            .map(e -> new MessageData().setEmbeds(e))
            .map(m -> Map.of(Optional.<String>empty(), m))
            .orElse(Map.of());
    }
}
