package ru.demetrious.deus.bot.adapter.output.freesteam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.demetrious.deus.bot.adapter.output.freesteam.dto.RssDto;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@FeignClient(
    name = "free-steam",
    url = "${feign.svc.free-steam.url}",
    path = "${feign.svc.free-steam.path}"
)
public interface FreeSteamClient {
    @GetMapping(produces = APPLICATION_XML_VALUE)
    RssDto getFreebieRss();
}
