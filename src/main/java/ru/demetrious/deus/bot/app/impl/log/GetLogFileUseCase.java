package ru.demetrious.deus.bot.app.impl.log;

import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.log.GetLogFileInbound;
import ru.demetrious.deus.bot.app.api.log.GetLogListOutbound;
import ru.demetrious.deus.bot.domain.Log;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.rightPad;

@RequiredArgsConstructor
@Component
public class GetLogFileUseCase implements GetLogFileInbound {
    private final GetLogListOutbound getLogListOutbound;

    @Override
    public byte[] getLogFile(Instant after) {
        return getLogListOutbound.getLogList().stream()
            .filter(log -> log.getTimestamp().isAfter(after))
            .sorted(comparing(Log::getTimestamp))
            .map(log -> join(" ",
                String.valueOf(log.getId()),
                String.valueOf(log.getTimestamp()),
                rightPad(log.getLevel(), 5),
                "[%s]".formatted(rightPad(abbreviate(Objects.toString(log.getThread(), EMPTY), 24), 24)),
                rightPad(log.getLogger(), 80),
                ":",
                log.getMessage(),
                log.getException()))
            .collect(joining("\n"))
            .getBytes(UTF_8);
    }
}
