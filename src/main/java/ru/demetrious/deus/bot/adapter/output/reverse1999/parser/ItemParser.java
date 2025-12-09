package ru.demetrious.deus.bot.adapter.output.reverse1999.parser;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiClient;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;

import static java.lang.Integer.parseInt;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.function.Failable.asFunction;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemParser {
    private static final Pattern MATERIAL_CRAFTS_PATTERN = compile("<span[^>]*>配方</span>.*?(<table[^>]*>.*?</table>)");
    private static final String MATERIAL_IMAGE_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[2]/div[1]/img";

    private final Reverse1999WikiClient reverse1999WikiClient;

    public static Map.Entry<Integer, Integer> parseMaterial(Node material) {
        return entry(parseMaterialId(material), parseMaterialValue(material));
    }

    public static int parseMaterialId(Node material) {
        return parseInt(material.attr("data-itemid").split("/")[1]);
    }

    public void rebuildItemTree(Element material, ConcurrentMap<Integer, ItemData> itemDataMap) {
        int id = parseMaterialId(material);

        if (itemDataMap.containsKey(id)) {
            return;
        }

        Optional<String> materialHtml = Optional.of(material)
            .map(f -> f.select("span>*[href]"))
            .map(Elements::first)
            .map(node -> node.attr("href"))
            .map(href -> decode(href, UTF_8))
            .map(reverse1999WikiClient::getHtml);

        itemDataMap.putIfAbsent(id, new ItemData()
            .setImage(materialHtml
                .map(Jsoup::parse)
                .map(document -> document.selectXpath(MATERIAL_IMAGE_XPATH))
                .map(img -> img.attr("src"))
                .map(URI::create)
                .map(reverse1999WikiClient::getImage)
                .map(ByteArrayInputStream::new)
                .map(asFunction(ImageIO::read))
                .map(Image::new)
                .orElse(null))
            .setCraft(materialHtml
                .map(MATERIAL_CRAFTS_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(Jsoup::parse)
                .map(document -> document.select("table th, table td")).stream()
                .flatMap(Collection::stream)
                .map(Element::firstElementChild)
                .filter(Objects::nonNull)
                .peek(e -> rebuildItemTree(e, itemDataMap))
                .map(ItemParser::parseMaterial)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static int parseMaterialValue(Node material) {
        String value = ofNullable(material.lastChild())
            .map(Node::lastChild)
            .map(Node::toString)
            .map(v -> v.substring(1))
            .orElseThrow();

        if (value.endsWith("K")) {
            return parseInt(value.substring(0, value.length() - 1)) * 1000;
        }

        return parseInt(value);
    }
}
