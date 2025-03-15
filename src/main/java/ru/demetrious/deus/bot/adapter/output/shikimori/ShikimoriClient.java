package ru.demetrious.deus.bot.adapter.output.shikimori;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.demetrious.deus.bot.domain.graphql.Request;
import ru.demetrious.deus.bot.domain.graphql.Response;
import ru.demetrious.deus.bot.fw.config.feign.FeignConfig;

import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.SHIKIMORI_REGISTRATION_ID;

@FeignClient(
    name = SHIKIMORI_REGISTRATION_ID,
    url = "${feign.svc.shikimori.url}",
    path = "${feign.svc.shikimori.path}",
    configuration = FeignConfig.class
)
public interface ShikimoriClient {
    @PostMapping
    Response execute(Request requestShikimori);
}
