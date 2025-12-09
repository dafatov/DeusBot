package ru.demetrious.deus.bot.adapter.output.reverse1999.parser;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiClient;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData.Consume;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;

import static java.lang.Integer.valueOf;
import static java.net.URI.create;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static org.jsoup.Jsoup.parse;
import static ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiAdapter.ID_PATTERN;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfException;
import static ru.demetrious.deus.bot.utils.ImageUtils.loadImage;

@Slf4j
@RequiredArgsConstructor
@Component
public class CharacterParser {
    private static final String CHARACTERS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[2]/div";
    private static final String CHARACTER_URL_XPATH = "./*[2]/*[1]/*[1]/*[1]";
    private static final String CHARACTER_NAME_XPATH = "/html/body/div[2]/div/div[1]/main/article/header/h1/span/span/small";
    private static final String CHARACTER_NAME_IMG_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[2]/div[5]/div/div/img";
    private static final String CHARACTER_POSTER_HTML_URL_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[1]/div[1]/div[2]";
    private static final String CHARACTER_POSTER_URL_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div[1]/div/a";
    private static final String CHARACTER_STARS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[2]/div[1]/div/img";
    private static final String MATERIAL_REGEX = "<h2(?![^>]display:\\snone)[^>]*>(?:(?!</h2>).)*?%s(?:(?!</h2>).)*?</h2>([\\s\\S]*?)(?=<h2(?![^>]display:\\snone)[^>]*>|$)";
    private static final Pattern INSIGHT_MATERIALS_PATTERN = compile(MATERIAL_REGEX.formatted("Insight Materials"));
    private static final Pattern RESONANCE_MATERIALS_PATTERN = compile(MATERIAL_REGEX.formatted("(?:Resonance Materials|Reasoning Materials)"));

    private final Reverse1999WikiClient reverse1999WikiClient;
    private final ItemParser itemParser;

    public Map<Integer, CharacterData> parseCharacters(ConcurrentMap<Integer, ItemData> itemDataMap) {
        ConcurrentMap<Integer, CharacterData> characterMap = new ConcurrentHashMap<>();
        Elements elements = parse(reverse1999WikiClient.getCharacterListHtml()).selectXpath(CHARACTERS_XPATH);

        elements.parallelStream().forEach(element -> parseCharacter(itemDataMap, element, characterMap));
        return characterMap;
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void parseCharacter(ConcurrentMap<Integer, ItemData> itemDataMap, Element element, ConcurrentMap<Integer, CharacterData> characterMap) {
        CharacterData character = new CharacterData();
        String url = decode(element.selectXpath(CHARACTER_URL_XPATH).attr("href"), UTF_8);
        String characterHtml = reverse1999WikiClient.getHtml(url);
        Matcher matcher = ID_PATTERN.matcher(characterHtml);

        if (!matcher.find()) {
            log.warn("Can't find character with url={}", url);
            return;
        }

        Integer id = valueOf(matcher.group(1));

        try {
            Document documentCharacter = parse(characterHtml);

            character.setId(id)
                .setName(getName(documentCharacter))
                .setNameImage(getNameImage(documentCharacter))
                .setAvatar(getAvatar(documentCharacter))
                .setRarity(getRarity(documentCharacter))
                .setConsumeData(getConsumeData(itemDataMap, url, characterHtml));
        } catch (Exception e) {
            log.warn("Can't map character with url={}", url);
        } finally {
            characterMap.put(id, character);
        }
    }

    private Consume getConsumeData(ConcurrentMap<Integer, ItemData> itemDataMap, String url, String characterHtml) {
        return new Consume()
            .setInsight(defaultIfException(() -> findMaterialConsumes(url, characterHtml, INSIGHT_MATERIALS_PATTERN, 1, itemDataMap)))
            .setResonance(defaultIfException(() -> findMaterialConsumes(url, characterHtml, RESONANCE_MATERIALS_PATTERN, 2, itemDataMap)));
    }

    private Image getAvatar(Document documentCharacter) {
        return defaultIfException(() -> {
            String imageHtmlUrl = decode(documentCharacter.selectXpath(CHARACTER_POSTER_HTML_URL_XPATH).attr("data-tabberimglink"), UTF_8);
            Document imageDocument = parse(reverse1999WikiClient.getHtml("/wiki/%s".formatted(imageHtmlUrl)));
            URI uri = create(imageDocument.selectXpath(CHARACTER_POSTER_URL_XPATH).attr("href"));

            return loadImage(reverse1999WikiClient.getImage(uri));
        });
    }

    private Image getNameImage(Document documentCharacter) {
        return defaultIfException(() -> {
            URI uri = create(documentCharacter.selectXpath(CHARACTER_NAME_IMG_XPATH).attr("src"));

            return loadImage(reverse1999WikiClient.getImage(uri));
        });
    }

    private Map<Integer, Map<Integer, Integer>> findMaterialConsumes(String url, String characterHtml, Pattern pattern, int initialValue,
                                                                     ConcurrentMap<Integer, ItemData> itemDataMap) {
        Matcher matcher = pattern.matcher(characterHtml);
        AtomicInteger counter = new AtomicInteger(initialValue);

        if (!matcher.find()) {
            throw new IllegalStateException("Can't find character's materials with url=" + url);
        }

        return parse(matcher.group(1)).selectXpath("/html/body/div/div").stream()
            .map(insight -> insight.selectXpath("./div[2]/span").stream()
                .peek(e -> itemParser.rebuildItemTree(e, itemDataMap))
                .map(ItemParser::parseMaterial)
                .collect(collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue), m -> entry(counter.getAndIncrement(), m))))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Integer getRarity(Document documentCharacter) {
        return defaultIfException(() -> documentCharacter.selectXpath(CHARACTER_STARS_XPATH).size());
    }

    private static String getName(Document documentCharacter) {
        return defaultIfException(() -> documentCharacter.selectXpath(CHARACTER_NAME_XPATH).text());
    }
}
