package ru.demetrious.deus.bot.adapter.output.anilist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.demetrious.deus.bot.domain.graphql.Request;
import ru.demetrious.deus.bot.domain.graphql.Response;
import ru.demetrious.deus.bot.fw.config.feign.FeignConfig;

import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.ANILIST_REGISTRATION_ID;

@FeignClient(
    name = ANILIST_REGISTRATION_ID,
    url = "${feign.svc.anilist.url}",
    path = "${feign.svc.anilist.path}",
    configuration = FeignConfig.class
)
public interface AnilistClient {
    @PostMapping
    Response execute(Request requestAnilist);
}
