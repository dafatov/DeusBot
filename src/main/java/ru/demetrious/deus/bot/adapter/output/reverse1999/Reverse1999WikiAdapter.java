package ru.demetrious.deus.bot.adapter.output.reverse1999;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.domain.Character;
import ru.demetrious.deus.bot.fw.annotation.cache.InitWarmUp;

import static java.lang.Integer.valueOf;
import static java.lang.Math.ceilDiv;
import static java.net.URI.create;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;
import static javax.imageio.ImageIO.read;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.jsoup.Jsoup.parse;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfException;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reverse1999WikiAdapter implements GetReverseCharacterListOutbound {
    private static final Pattern CHARACTER_ID_PATTERN = compile("编号ID\\D*(\\d+)");
    private static final String CHARACTERS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[2]/div";
    private static final String CHARACTER_URL_XPATH = "./*[2]/*[1]/*[1]/*[1]";
    private static final String CHARACTER_NAME_XPATH = "/html/body/div[2]/div/div[1]/main/article/header/h1/span/span/small";
    private static final String CHARACTER_NAME_IMG_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[2]/div[5]/div/div/img";
    private static final String CHARACTER_POSTER_HTML_URL_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[1]/div[1]/div[2]";
    private static final String CHARACTER_POSTER_URL_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div[1]/div/a";
    private static final String CHARACTER_STARS_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[1]/div[2]/div[1]/div/img";

    private final Reverse1999WikiClient reverse1999WikiClient;

    @InitWarmUp
    @Cacheable(value = "reverse1999-characters", sync = true)
    @Override
    public Map<Integer, Character> getReverseCharacterList() {
        Map<Integer, Character> characterMap = new HashMap<>();
        Elements elements = parse(reverse1999WikiClient.getCharacterListHtml()).selectXpath(CHARACTERS_XPATH);
        AtomicInteger counter = new AtomicInteger();

        elements.parallelStream().forEach(element -> {
            Character character = new Character();
            String url = decode(element.selectXpath(CHARACTER_URL_XPATH).attr("href"), UTF_8);
            String characterHtml = reverse1999WikiClient.getCharacterHtml(url);
            Matcher matcher = CHARACTER_ID_PATTERN.matcher(characterHtml);

            if (matcher.find()) {
                Integer id = valueOf(matcher.group(1));

                try {
                    Document documentCharacter = parse(characterHtml);

                    character.setId(id)
                        .setName(defaultIfException(() -> documentCharacter.selectXpath(CHARACTER_NAME_XPATH).text()))
                        .setNameImage(defaultIfException(() -> {
                            URI nameUri = create(documentCharacter.selectXpath(CHARACTER_NAME_IMG_XPATH).attr("src"));

                            return read(new ByteArrayInputStream(reverse1999WikiClient.getImage(nameUri)));
                        }))
                        .setAvatar(defaultIfException(() -> {
                            String imageHtmlUrl = decode(documentCharacter.selectXpath(CHARACTER_POSTER_HTML_URL_XPATH).attr("data-tabberimglink"), UTF_8);
                            Document imageDocument = parse(reverse1999WikiClient.getCharacterHtml("/wiki/%s".formatted(imageHtmlUrl)));
                            URI imageUri = create(imageDocument.selectXpath(CHARACTER_POSTER_URL_XPATH).attr("href"));

                            return read(new ByteArrayInputStream(reverse1999WikiClient.getImage(imageUri)));
                        }))
                        .setRarity(defaultIfException(() -> documentCharacter.selectXpath(CHARACTER_STARS_XPATH).size()));
                } finally {
                    characterMap.put(id, character);
                }
            } else {
                log.warn("Can't find character with url={}", url);
            }
            log.debug("Progress: {}%", leftPad(String.valueOf(ceilDiv(100 * counter.getAndIncrement(), elements.size())), 3, SPACE));
        });
        return characterMap;
    }
}
