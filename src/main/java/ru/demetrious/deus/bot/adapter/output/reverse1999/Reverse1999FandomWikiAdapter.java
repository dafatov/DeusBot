package ru.demetrious.deus.bot.adapter.output.reverse1999;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.promocode.GetActualReversePromotionalCodes;

import static org.jsoup.Jsoup.parse;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reverse1999FandomWikiAdapter implements GetActualReversePromotionalCodes {
    private static final String XPATH = "//h2[span[@id='Active']]/following-sibling::table[preceding-sibling::h2[1][span[@id='Active']]]//tr/td[1]";

    private final Reverse1999FandomWikiClient reverse1999FandomWikiClient;

    @Override
    public List<String> getActualReversePromotionalCodes() {
        return parse(reverse1999FandomWikiClient.getPromotionalCodeHtml())
            .selectXpath(XPATH)
            .eachText();
    }
}
