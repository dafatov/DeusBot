package ru.demetrious.deus.bot.adapter.output.deus;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "deus-client",
    url = "${feign.svc.deus.url}",
    path = "${feign.svc.deus.path}"
)
public interface DeusClient {
    @PostMapping("/webhook/voice")
    String askByVoice(@RequestBody byte[] audio, @RequestParam("userId") String userId);
}
