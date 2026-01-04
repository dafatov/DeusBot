package ru.demetrious.deus.bot.adapter.output.reverse1999.parser;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.function.Failable.asFunction;
import static org.jsoup.Jsoup.parse;
import static ru.demetrious.deus.bot.adapter.output.reverse1999.Reverse1999WikiAdapter.ID_PATTERN;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemParser {
    private static final Pattern MATERIAL_CRAFTS_PATTERN = compile("<span[^>]*>配方</span>.*?(<table[^>]*>.*?</table>)");
    private static final String MATERIAL_IMAGE_XPATH = "/html/body/div[2]/div/div[1]/main/article/section[1]/div/div/div/div[2]/div[1]/img";
    private static final String MATERIAL_LIST_XPATH = "//*[@id=\"mw-content-text\"]/div/div[1]/div[2]/div";
    private static final List<String> ITEM_TYPES = List.of("11", "12", "18", "19", "62");

    private final Reverse1999WikiClient reverse1999WikiClient;

    private final Map<String, Integer> itemOrders = new HashMap<>();

    public static Map.Entry<Integer, Integer> parseMaterial(Node material) {
        return entry(parseMaterialId(material), parseMaterialValue(material));
    }

    public static int parseMaterialId(Node material) {
        return parseInt(material.attr("data-itemid").split("/")[1]);
    }

    public void rebuildItemTree(Element material, ConcurrentMap<Integer, ItemData> itemDataMap) {
        itemDataMap.computeIfAbsent(parseMaterialId(material), key -> {
            Optional<String> materialUrl = Optional.of(material)
                .map(e -> e.select("span>*[href]"))
                .map(Elements::first)
                .map(node -> node.attr("href"));
            Optional<String> materialHtml = materialUrl
                .map(href -> decode(href, UTF_8))
                .map(reverse1999WikiClient::getHtml);

            return new ItemData()
                .setOrder(materialUrl
                    .map(itemOrders::remove)
                    .orElse(MAX_VALUE))
                .setImage(getImage(materialHtml))
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
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
        });
    }

    public void initItemOrders() {
        Elements elements = parse(reverse1999WikiClient.getMaterialListHtml()).selectXpath(MATERIAL_LIST_XPATH);
        Map<String, List<Element>> groupedByType = new HashMap<>();

        for (Element element : elements) {
            String type = element.attr("data-subtype");

            if (!ITEM_TYPES.contains(type)) {
                continue;
            }

            groupedByType.computeIfAbsent(type, k -> new ArrayList<>())
                .add(element);
        }

        int index = 0;
        for (String type : ITEM_TYPES) {
            List<Element> itemsOfType = groupedByType.get(type);

            if (type.equals("11")) {
                Map<String, List<Element>> itemsByRare = new LinkedHashMap<>();

                for (Element item : itemsOfType) {
                    String rare = item.attr("data-rare");

                    itemsByRare.computeIfAbsent(rare, k -> new ArrayList<>())
                        .addFirst(item);
                }

                itemsOfType = itemsByRare.values().stream()
                    .flatMap(List::stream)
                    .toList();
            }

            for (Element element : itemsOfType) {
                Element hrefElement = element.select("a[href]").first();

                if (isNull(hrefElement)) {
                    continue;
                }

                this.itemOrders.put(hrefElement.attr("href"), index++);
            }
        }
    }

    public void parseOtherItems(ConcurrentMap<Integer, ItemData> itemDataMap) {
        itemOrders.entrySet().parallelStream().forEach((entry) -> {
            String html = reverse1999WikiClient.getHtml(decode(entry.getKey(), UTF_8));
            Matcher matcher;

            if (isNull(html) || !(matcher = ID_PATTERN.matcher(html)).find()) {
                return;
            }

            itemDataMap.putIfAbsent(parseInt(matcher.group(1).split("#")[1]), new ItemData()
                .setOrder(entry.getValue())
                .setImage(getImage(Optional.of(html))));
        });
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Image getImage(Optional<String> materialHtml) {
        return materialHtml
            .map(Jsoup::parse)
            .map(document -> document.selectXpath(MATERIAL_IMAGE_XPATH))
            .map(img -> img.attr("src"))
            .map(URI::create)
            .map(reverse1999WikiClient::getImage)
            .map(ByteArrayInputStream::new)
            .map(asFunction(ImageIO::read))
            .map(Image::new)
            .orElse(null);
    }

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
