package ru.demetrious.deus.bot.adapter.output.arting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.demetrious.deus.bot.adapter.output.arting.dto.CreateDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.GetDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.PayloadDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.ResponseDto;

import static org.springframework.http.HttpHeaders.COOKIE;

@FeignClient(
    name = "arting",
    url = "${feign.svc.arting.url}",
    path = "${feign.svc.arting.path}"
)
public interface ArtingClient {
    @PostMapping("/cg/text-to-image/create")
    ResponseDto<CreateDto> create(@RequestHeader(COOKIE) String cookie, PayloadDto payloadDto);

    @PostMapping("/cg/text-to-image/get")
    ResponseDto<GetDto> get(@RequestHeader(COOKIE) String cookie, CreateDto createDto);
}
