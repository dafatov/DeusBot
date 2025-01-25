package ru.demetrious.deus.bot.adapter.output.repository.log;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.log.GetLogListOutbound;
import ru.demetrious.deus.bot.app.api.log.RemoveLogsOutbound;
import ru.demetrious.deus.bot.domain.Log;

@Transactional
@RequiredArgsConstructor
@Component
public class LogAdapter implements GetLogListOutbound, RemoveLogsOutbound {
    private final LogRepository logRepository;

    @Override
    public List<Log> getLogList() {
        return logRepository.findAll();
    }

    @Override
    public List<Log> removeLogs(Instant before) {
        return logRepository.deleteAllByTimestampBefore(before);
    }
}
