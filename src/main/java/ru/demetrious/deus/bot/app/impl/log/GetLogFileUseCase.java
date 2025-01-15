package ru.demetrious.deus.bot.app.impl.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.log.GetLogFileInbound;
import ru.demetrious.deus.bot.app.api.log.GetLogListOutbound;
import ru.demetrious.deus.bot.domain.Log;

import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;

@RequiredArgsConstructor
@Component
public class GetLogFileUseCase implements GetLogFileInbound {
    private final GetLogListOutbound getLogListOutbound;

    @Override
    public byte[] getLogFile() {
        return getLogListOutbound.getLogList().stream()
            .sorted(comparing(Log::getTimestamp))
            .map(log -> join(" ",
                String.valueOf(log.getId()),
                String.valueOf(log.getTimestamp()),
                leftPad(log.getLevel(), 5),
                rightPad(log.getLogger() + ":", 80),
                log.getMessage(),
                log.getException()))
            .collect(joining("\n"))
            .getBytes();
    }
}
