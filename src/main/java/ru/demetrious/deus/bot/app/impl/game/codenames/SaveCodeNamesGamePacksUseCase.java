package ru.demetrious.deus.bot.app.impl.game.codenames;

import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.demetrious.deus.bot.app.api.game.codenames.SaveCodeNamesGamePacksInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.SaveCodeNamesGamePacksOutbound;
import ru.demetrious.deus.bot.domain.game.Pack;
import ru.demetrious.deus.bot.domain.game.Word;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.LF;

@Slf4j
@RequiredArgsConstructor
@Component
public class SaveCodeNamesGamePacksUseCase implements SaveCodeNamesGamePacksInbound {
    private final SaveCodeNamesGamePacksOutbound saveCodeNamesGamePacksOutbound;

    @Override
    public void savePacks(MultipartFile[] packs) {
        if (isNull(packs)) {
            log.debug("packs is null");
            return;
        }

        for (MultipartFile file : packs) {
            try {
                String filename = file.getOriginalFilename();
                String[] string = new String(file.getBytes(), UTF_8).split(LF);
                Set<Word> wordSet = stream(string)
                    .map(StringUtils::trim)
                    .map(StringUtils::lowerCase)
                    .filter(StringUtils::isNotBlank)
                    .map(Word::of)
                    .collect(toSet());
                Pack pack = new Pack();

                if (wordSet.size() < 25) {
                    throw new IllegalStateException("word set size is less than 25");
                }

                pack.setName(filename);
                pack.setWords(wordSet);
                saveCodeNamesGamePacksOutbound.savePack(pack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
