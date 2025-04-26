package ru.demetrious.deus.bot.adapter.output.deus;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.demetrious.deus.bot.adapter.output.deus.dto.DeusContext;

@FeignClient(
    name = "deus-client",
    url = "${feign.svc.deus.url}",
    path = "${feign.svc.deus.path}"
)
public interface DeusClient {
    @PostMapping("/webhook/voice")
    void askByVoice(@RequestBody DeusContext context);
}
