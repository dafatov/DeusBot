package ru.demetrious.deus.bot.app.impl.publication;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.demetrious.deus.bot.app.api.message.PublishMessageOutbound;
import ru.demetrious.deus.bot.app.api.publicist.GetPublicistListOutbound;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.util.Optional.ofNullable;

public abstract class PublicationJob extends QuartzJobBean {
    @Autowired
    private GetPublicistListOutbound getPublicistListOutbound;
    @Autowired
    private PublishMessageOutbound publishMessageOutbound;

    abstract Map<Optional<String>, MessageData> supplyMessage(@NotNull JobExecutionContext context);

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        Map<Optional<String>, MessageData> optionalMessageDataMap = supplyMessage(context);

        getPublicistListOutbound.getPublisistList().stream()
            .flatMap(publicist -> Stream.of(
                Pair.of(publicist.getChannelId(), ofNullable(optionalMessageDataMap.get(ofNullable(publicist.getGuildId())))),
                Pair.of(publicist.getChannelId(), ofNullable(optionalMessageDataMap.get(Optional.<String>empty())))
            )).filter(p -> p.getRight().isPresent())
            .forEach(stringOptionalPair -> publishMessageOutbound.publish(stringOptionalPair.getLeft(), stringOptionalPair.getRight().get()));
    }
}
