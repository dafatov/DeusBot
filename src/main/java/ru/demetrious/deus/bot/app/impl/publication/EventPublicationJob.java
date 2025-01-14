package ru.demetrious.deus.bot.app.impl.publication;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.DESCRIPTION;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.GUILD_ID;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.TITLE;
import static ru.demetrious.deus.bot.app.impl.event.EventComponent.USER_ID;

@RequiredArgsConstructor
@Component
public class EventPublicationJob extends PublicationJob {
    @Override
    Map<Optional<String>, MessageData> supplyMessage(@NotNull JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        MessageData messageData = new MessageData();

        ofNullable(jobDataMap.getString(USER_ID))
            .map(userId -> "<@" + userId + ">")
            .ifPresent(messageData::setContent);
        return Map.of(ofNullable(jobDataMap.getString(GUILD_ID)), messageData.setEmbeds(List.of(new MessageEmbed()
            .setTitle(jobDataMap.getString(TITLE))
            .setDescription(jobDataMap.getString(DESCRIPTION)))));
    }
}
