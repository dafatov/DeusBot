package ru.demetrious.deus.bot.adapter.output.shikimori;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.demetrious.deus.bot.adapter.output.shikimori.dto.UserDto;
import ru.demetrious.deus.bot.fw.config.feign.FeignConfig;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.SHIKIMORI_REGISTRATION_ID;

@FeignClient(
    name = SHIKIMORI_REGISTRATION_ID,
    url = "${feign.svc.shikimori.url}",
    path = "${feign.svc.shikimori.path}",
    configuration = FeignConfig.class
)
public interface ShikimoriClient {
    @GetMapping("/api/users/whoami")
    UserDto getMe();

    @GetMapping(value = "/{nickname}/list_export/animes.xml", produces = APPLICATION_XML_VALUE)
    Map<String, ?> getAnimeListXml(@PathVariable("nickname") String nickname);
}
