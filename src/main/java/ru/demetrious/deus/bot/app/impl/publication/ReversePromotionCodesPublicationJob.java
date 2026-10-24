package ru.demetrious.deus.bot.app.impl.publication;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.promocode.GetActualReversePromotionalCodes;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.fw.annotation.quartz.InitScheduled;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.subtract;
import static org.apache.commons.lang3.StringUtils.LF;
import static ru.demetrious.deus.bot.utils.JacksonUtils.parse;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@InitScheduled(name = "reverse-promotion-codes", groupName = "publication", cron = "0 0 0/1 ? * *")
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@RequiredArgsConstructor
@Component
public class ReversePromotionCodesPublicationJob extends PublicationJob {
    private static final String PREVIOUS_PROMOTION_CODES = "previousPromotionCodes";
    private static final TypeReference<List<String>> TYPE_REFERENCE = new TypeReference<>() {
    };

    private final GetActualReversePromotionalCodes getActualReversePromotionalCodes;

    @Override
    Map<Optional<String>, MessageData> supplyMessage(@NotNull JobExecutionContext context) {
        List<String> actualReversePromotionalCodes = getActualReversePromotionalCodes.getActualReversePromotionalCodes();
        List<String> previousPromoCodes = ofNullable(context.getMergedJobDataMap().getString(PREVIOUS_PROMOTION_CODES))
            .map(f -> parse(f, TYPE_REFERENCE))
            .orElseGet(List::of);

        context.getJobDetail().getJobDataMap().put(PREVIOUS_PROMOTION_CODES, writeValueAsString(actualReversePromotionalCodes));
        return of(subtract(actualReversePromotionalCodes, previousPromoCodes).stream()
            .map("- `%s`"::formatted)
            .collect(joining(LF)))
            .filter(StringUtils::isNotBlank)
            .map(new MessageEmbed().setTitle("Новые актуальные промокоды Reverse 1999")::setDescription)
            .map(List::of)
            .map(new MessageData()::setEmbeds)
            .map(m -> Map.of(Optional.<String>empty(), m))
            .orElseGet(Map::of);
    }
}
