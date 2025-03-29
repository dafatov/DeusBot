package ru.demetrious.deus.bot.adapter.input.file;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.app.api.log.GetLogFileInbound;

import static java.time.Instant.MIN;
import static ru.demetrious.deus.bot.utils.CustomMediaType.TEXT_PLAIN_UTF_8;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileAdapter {
    private final GetLogFileInbound getLogFileInbound;

    @RequestMapping(value = "/logs", produces = TEXT_PLAIN_UTF_8)
    public byte[] logs(@RequestParam Optional<Instant> after) {
        return getLogFileInbound.getLogFile(after.orElse(MIN));
    }
}
