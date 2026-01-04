package ru.demetrious.deus.bot.adapter.output.reverse1999;

import feign.Headers;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@FeignClient(
    name = "reverse1999-wiki",
    url = "${feign.svc.reverse1999-wiki.url}",
    path = "${feign.svc.reverse1999-wiki.path}",
    dismiss404 = true
)
public interface Reverse1999WikiClient {
    String USER_AGENT = "User-Agent=PostmanRuntime/7.45.0";
    String POSTMAN_TOKEN = "Postman-Token=d29c25ef-e055-4342-b30a-1b030d559054";

    @Retry(name = "reverse1999-wiki")
    @GetMapping(value = "/wiki/%E8%A7%92%E8%89%B2%E5%88%97%E8%A1%A8", produces = TEXT_HTML_VALUE, headers = {USER_AGENT, POSTMAN_TOKEN})
    String getCharacterListHtml();

    @Retry(name = "reverse1999-wiki")
    @GetMapping(value = "/wiki/%E7%AB%A0%E8%8A%82%E5%88%97%E8%A1%A8/%E4%B8%BB%E7%BA%BF%E6%95%85%E4%BA%8B", produces = TEXT_HTML_VALUE, headers = {USER_AGENT, POSTMAN_TOKEN})
    String getMainLevelListHtml();

    @Retry(name = "reverse1999-wiki")
    @GetMapping(value = "/wiki/%E7%AB%A0%E8%8A%82%E5%88%97%E8%A1%A8/%E6%9B%B4%E5%A4%9A", produces = TEXT_HTML_VALUE, headers = {USER_AGENT, POSTMAN_TOKEN})
    String getExtraLevelListHtml();

    @Retry(name = "reverse1999-wiki")
    @GetMapping(value = "/wiki/%E7%89%A9%E5%93%81%E5%88%97%E8%A1%A8", produces = TEXT_HTML_VALUE, headers = {USER_AGENT, POSTMAN_TOKEN})
    String getMaterialListHtml();

    @Retry(name = "reverse1999-wiki")
    @GetMapping(value = "{url}", produces = TEXT_HTML_VALUE, headers = {USER_AGENT, POSTMAN_TOKEN})
    String getHtml(@PathVariable String url);

    @GetMapping
    @Headers({USER_AGENT, POSTMAN_TOKEN})
    byte[] getImage(URI uri);
}
