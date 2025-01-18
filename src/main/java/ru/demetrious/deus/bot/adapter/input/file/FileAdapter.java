package ru.demetrious.deus.bot.adapter.input.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.app.api.log.GetLogFileInbound;

import static ru.demetrious.deus.bot.utils.CustomMediaType.TEXT_PLAIN_UTF_8;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileAdapter {
    private final GetLogFileInbound getLogFileInbound;

    @RequestMapping(value = "/logs", produces = TEXT_PLAIN_UTF_8)
    public byte[] logs() {
        return getLogFileInbound.getLogFile();
    }
}
