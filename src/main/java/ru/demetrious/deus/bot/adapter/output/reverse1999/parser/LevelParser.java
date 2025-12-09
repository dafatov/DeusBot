package ru.demetrious.deus.bot.adapter.output.reverse1999.parser;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.FuzzyMappingStrategyBuilder;
import com.opencsv.bean.MappingStrategy;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiClient;
import ru.demetrious.deus.bot.adapter.output.reverse1999.dto.LevelItemsRateDto;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;
import ru.demetrious.deus.bot.domain.reverse1999.LevelData;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.Map.of;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.function.Failable.asFunction;
import static org.jsoup.Jsoup.parse;
import static ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiAdapter.ID_PATTERN;
import static ru.demetrious.deus.bot.adapter.output.reverse1999.parser.ItemParser.parseMaterialId;
import static ru.demetrious.deus.bot.domain.reverse1999.LevelData.Drop;
import static ru.demetrious.deus.bot.domain.reverse1999.LevelData.Drop.Probability.COMMON;
import static ru.demetrious.deus.bot.domain.reverse1999.LevelData.Drop.Probability.FIXED;
import static ru.demetrious.deus.bot.domain.reverse1999.LevelData.Drop.Probability.POSSIBLE;
import static ru.demetrious.deus.bot.domain.reverse1999.LevelData.Drop.Probability.RARE;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfException;

@Slf4j
@RequiredArgsConstructor
@Component
public class LevelParser {
    private static final String MAIN_LEVELS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div[2]/div[2]/div[position() > 1]";
    private static final String EXTRA_LEVELS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div[2]";
    private static final String MAIN_LEVEL_REWARD_XPATH = "div#mw-customcollapsible-reward2";
    private static final String EXTRA_LEVEL_REWARD_XPATH = "div.battle-info:has(div.battle-info-title:contains(奖励)):not(:has(div.battle-info-empty))";
    private static final String REWARD_MATERIALS_XPATH = "span.item-link:has(div.item-bonus-probability:not(:empty))";
    private static final Pattern VERSION_PATTERN = compile("Ver(\\d+\\.\\d+)");
    private static final Map<Integer, Integer> ID_MATHER = Map.<Integer, Integer>ofEntries(
        entry(21001, 21002),
        entry(21002, 21004),
        entry(21003, 21006),
        entry(21004, 21007),
        entry(21005, 21009),
        entry(21006, 21013),
        entry(20501, 20502),
        entry(20502, 20503),
        entry(20503, 20504),
        entry(20504, 20505),
        entry(21007, 21017),
        entry(20505, 20506),
        entry(20506, 20507),
        entry(21008, 21019),
        entry(20507, 20508),
        entry(20508, 20509),
        entry(21009, 21021),
        entry(20509, 20510),
        entry(21010, 21022),
        entry(20510, 20512),
        entry(20511, 20514),
        entry(20512, 20516),
        entry(20513, 20519),
        entry(20514, 20520),
        entry(21101, 21102),
        entry(21102, 21104),
        entry(21103, 21105),
        entry(21104, 21109),
        entry(21105, 21111),
        entry(20601, 20603),
        entry(20602, 20604),
        entry(20603, 20605),
        entry(20604, 20606),
        entry(21106, 21118),
        entry(20605, 20607),
        entry(21107, 21119),
        entry(20606, 20608),
        entry(21108, 21120),
        entry(21109, 21121),
        entry(20607, 20610),
        entry(21110, 21122),
        entry(20608, 20612),
        entry(20609, 20614),
        entry(20610, 20615),
        entry(20102, 20104),
        entry(20103, 20105),
        entry(20104, 20106),
        entry(20105, 20107),
        entry(20611, 20619),
        entry(20106, 20108),
        entry(20612, 20621),
        entry(20107, 20111),
        entry(20108, 20112),
        entry(20613, 20624),
        entry(20109, 20114),
        entry(20110, 20115),
        entry(20111, 20116),
        entry(20701, 20703),
        entry(20702, 20705),
        entry(20703, 20706),
        entry(20704, 20707),
        entry(20705, 20708),
        entry(20706, 20710),
        entry(20707, 20712),
        entry(20708, 20713),
        entry(20202, 20203),
        entry(20203, 20204),
        entry(20709, 20716),
        entry(20204, 20205),
        entry(20205, 20206),
        entry(20710, 20718),
        entry(20711, 20719),
        entry(20206, 20208),
        entry(20712, 20720),
        entry(20207, 20209),
        entry(20208, 20210),
        entry(20209, 20211),
        entry(20210, 20212),
        entry(20713, 20724),
        entry(20211, 20213),
        entry(20212, 20214),
        entry(20714, 20726),
        entry(20801, 20803),
        entry(20802, 20805),
        entry(20803, 20807),
        entry(20804, 20809),
        entry(20805, 20810),
        entry(20806, 20811),
        entry(20301, 20303),
        entry(20807, 20815),
        entry(20808, 20816),
        entry(20302, 20305),
        entry(20809, 20817),
        entry(20303, 20306),
        entry(20810, 20818),
        entry(20304, 20307),
        entry(20811, 20819),
        entry(20305, 20308),
        entry(20812, 20820),
        entry(20306, 20309),
        entry(20813, 20821),
        entry(20307, 20311),
        entry(20308, 20313),
        entry(20309, 20315),
        entry(10103, 10104),
        entry(10104, 10105),
        entry(10105, 10106),
        entry(10106, 10107),
        entry(10107, 10108),
        entry(10110, 10111),
        entry(10111, 10112),
        entry(10113, 10114),
        entry(10120, 10115),
        entry(10115, 10116),
        entry(20902, 20903),
        entry(20903, 20905),
        entry(20904, 20906),
        entry(20905, 20907),
        entry(20906, 20908),
        entry(20907, 20911),
        entry(20908, 20913),
        entry(20401, 20403),
        entry(20909, 20915),
        entry(20402, 20404),
        entry(20910, 20916),
        entry(20403, 20405),
        entry(20404, 20406),
        entry(20911, 20918),
        entry(20405, 20410),
        entry(20406, 20411),
        entry(20407, 20413),
        entry(20408, 20416),
        entry(20409, 20418),
        entry(20410, 20420),
        entry(20411, 20421)
    );

    private final Reverse1999WikiClient reverse1999WikiClient;
    private final ItemParser itemParser;

    @Value("classpath:reverse1999/level/level_items_rate.csv")
    private Resource levelItemRatesResource;

    public Map<Integer, LevelData> parseLevels(ConcurrentMap<Integer, ItemData> itemDataMap) {
        ConcurrentMap<Integer, LevelData> levelMap = new ConcurrentHashMap<>();
        Map<Integer, Map<Integer, Double>> levelItemRates = getLevelItemRates();
        Elements document = parse(reverse1999WikiClient.getMainLevelListHtml()).selectXpath(MAIN_LEVELS_XPATH);
        document.addAll(parse(reverse1999WikiClient.getExtraLevelListHtml()).selectXpath(EXTRA_LEVELS_XPATH));

        document.select("div.episode-list a[href]").parallelStream()
            .map(element -> decode(element.attr("href"), UTF_8))
            .distinct()
            // Пропускаем уровни с психокубами
            .filter(url -> !contains(url, "PA"))
            .forEach(url -> {
                Document documentLevel = parse(reverse1999WikiClient.getHtml(url));
                Elements select = documentLevel.select("div.tabber-item.tabber");

                if (select.isEmpty()) {
                    parseLevel(documentLevel, url, levelMap, itemDataMap, levelItemRates);
                } else {
                    select.forEach(tab -> parseLevel(tab, url, levelMap, itemDataMap, levelItemRates));
                }
            });
        return levelMap;
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Map<Integer, Map<Integer, Double>> getLevelItemRates() {
        MappingStrategy<LevelItemsRateDto> strategy = new FuzzyMappingStrategyBuilder<LevelItemsRateDto>().build();

        strategy.setType(LevelItemsRateDto.class);
        try (InputStreamReader inputStreamReader = new InputStreamReader(levelItemRatesResource.getInputStream())) {
            return new CsvToBeanBuilder<LevelItemsRateDto>(inputStreamReader)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withMappingStrategy(strategy)
                .build()
                .parse().stream()
                .collect(groupingBy(LevelItemsRateDto::getId, collectingAndThen(toList(), LevelParser::buildInnerMap)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseLevel(Element tab, String url, ConcurrentMap<Integer, LevelData> levelMap,
                            ConcurrentMap<Integer, ItemData> itemDataMap, Map<Integer, Map<Integer, Double>> levelsItemRates) {
        Optional<Element> rewardElement = ofNullable(tab.selectFirst(MAIN_LEVEL_REWARD_XPATH))
            .or(() -> ofNullable(tab.selectFirst(EXTRA_LEVEL_REWARD_XPATH)));

        if (rewardElement.isEmpty()) {
            return;
        }

        Matcher matcher = ID_PATTERN.matcher(tab.select("div.episode-desc").text());

        if (!matcher.find()) {
            return;
        }

        LevelData levelData = new LevelData();
        Integer id = valueOf(matcher.group(1));

        try {
            String name = url.split("/")[2] + Optional.of(tab.attr("data-index"))
                .map(index -> defaultIfException(() -> parseInt(index), 0, true))
                .filter(index -> index > 1)
                .map(index -> " Hard")
                .orElse(EMPTY);
            Map<Integer, Double> levelItemsRates = levelsItemRates.getOrDefault(ID_MATHER.getOrDefault(id, id), of());
            Elements items = rewardElement.get().select(REWARD_MATERIALS_XPATH);

            levelData.setName(name);
            levelData.setCost(valueOf(tab.select("*.item-link-count").text()));
            items.forEach(item -> parseMaterialAsDrop(item).ifPresent(drop -> {
                int itemId = parseMaterialId(item);

                if (isNull(drop.getMathematicalExpectation())) {
                    drop.setMathematicalExpectation(levelItemsRates.getOrDefault(itemId, null));
                }
                if (isNull(drop.getMathematicalExpectation()) && drop.getProbability() == FIXED) {
                    // Для уровней с материалами для insight'а известно, что двойная награда у четных
                    if (containsAny(name, "ME", "SL", "SS", "BW") && containsAny(name, "2", "4", "6")) {
                        drop.setMathematicalExpectation(2.);
                    } else {
                        drop.setMathematicalExpectation(1.);
                    }
                }

                levelData.getDropMap().put(itemId, drop);
                itemParser.rebuildItemTree(item, itemDataMap);
            }));
        } catch (Exception e) {
            log.warn("Can't map level for url={}", url, e);
        } finally {
            if (!levelData.getDropMap().isEmpty()) {
                levelMap.put(id, levelData);
            }
        }
    }

    private static Optional<Drop> parseMaterialAsDrop(Element material) {
        Drop drop = new Drop();
        String probability = material.select("*.item-bonus-probability").text();

        drop.setProbability(switch (probability) {
            case "固定" -> FIXED;
            case "概率" -> POSSIBLE;
            case "大概率" -> COMMON;
            case "极小概率" -> RARE;
            case "首通" -> null;
            default -> {
                log.warn("Unexpected probability value: {}", probability);
                yield null;
            }
        });
        Optional.of(material.select("*.item-middle-count"))
            .map(Elements::text)
            .filter(StringUtils::isNumeric)
            .map(Double::valueOf)
            .ifPresent(drop::setMathematicalExpectation);
        return Optional.of(drop).filter(d -> nonNull(d.getProbability()));
    }

    private static Map<Integer, Double> buildInnerMap(List<LevelItemsRateDto> itemsRateDtoList) {
        return itemsRateDtoList.stream()
            .flatMap(levelItemsRateDto -> levelItemsRateDto.getJoinedFields().entries().stream()
                .filter(e -> e.getValue() >= 0)
                .map(entry -> extractVersion(levelItemsRateDto.getName())
                    .map(v -> entry(parseInt(entry.getKey()), entry(v, entry.getValue())))
                    .orElse(null))
                .filter(Objects::nonNull))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a.getKey() >= b.getKey() ? a : b))
            .entrySet().stream()
            .collect(toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
    }

    private static Optional<Double> extractVersion(String name) {
        return ofNullable(name)
            .map(VERSION_PATTERN::matcher)
            .filter(Matcher::find)
            .map(f -> f.group(1))
            .map(asFunction(Double::parseDouble));
    }
}
