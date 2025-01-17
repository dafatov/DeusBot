package ru.demetrious.deus.bot.adapter.input.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.app.api.log.GetLogFileInbound;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileAdapter {
    private final GetLogFileInbound getLogFileInbound;

    @RequestMapping(value = "/logs", produces = TEXT_PLAIN_VALUE)
    public byte[] logs() {
        return getLogFileInbound.getLogFile();
    }
}
