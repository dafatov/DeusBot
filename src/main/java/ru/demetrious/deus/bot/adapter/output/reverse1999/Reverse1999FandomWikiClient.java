package ru.demetrious.deus.bot.adapter.output.reverse1999;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "reverse1999-fandom-wiki",
    url = "${feign.svc.reverse1999-fandom-wiki.url}",
    path = "${feign.svc.reverse1999-fandom-wiki.path}",
    dismiss404 = true
)
public interface Reverse1999FandomWikiClient {
    @GetMapping(value = "/Promotional_Code")
    String getPromotionalCodeHtml();
}
