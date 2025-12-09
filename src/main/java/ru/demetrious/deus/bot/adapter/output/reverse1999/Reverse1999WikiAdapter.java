package ru.demetrious.deus.bot.adapter.output.reverse1999;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.reverse1999.parser.CharacterParser;
import ru.demetrious.deus.bot.adapter.output.reverse1999.parser.LevelParser;
import ru.demetrious.deus.bot.app.api.character.GetReverseDataOutbound;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;
import ru.demetrious.deus.bot.domain.reverse1999.LevelData;
import ru.demetrious.deus.bot.domain.reverse1999.ReverseData;
import ru.demetrious.deus.bot.fw.annotation.cache.InitWarmUp;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.collections4.MapUtils.unmodifiableMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reverse1999WikiAdapter implements GetReverseDataOutbound {
    public static final Pattern ID_PATTERN = compile("ID\\D*(\\d+)");

    private final CharacterParser characterParser;
    private final LevelParser levelParser;

    @InitWarmUp
    @Cacheable(value = "reverse1999-data", sync = true)
    @Override
    public ReverseData getReverseData() {
        ConcurrentMap<Integer, ItemData> itemDataMap = new ConcurrentHashMap<>();
        Map<Integer, CharacterData> characterMap = characterParser.parseCharacters(itemDataMap);
        Map<Integer, LevelData> levelMap = levelParser.parseLevels(itemDataMap);

        return new ReverseData()
            .setCharacters(unmodifiableMap(characterMap))
            .setLevels(unmodifiableMap(levelMap))
            .setItems(unmodifiableMap(itemDataMap));
    }
}
