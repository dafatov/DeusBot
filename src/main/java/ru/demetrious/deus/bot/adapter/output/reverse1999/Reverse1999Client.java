package ru.demetrious.deus.bot.adapter.output.reverse1999;


import java.net.URI;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.demetrious.deus.bot.adapter.output.reverse1999.dto.SummonsDto;

@FeignClient(name = "reverse1999")
public interface Reverse1999Client {
    @GetMapping
    Optional<SummonsDto> getSummons(URI uri);
}
