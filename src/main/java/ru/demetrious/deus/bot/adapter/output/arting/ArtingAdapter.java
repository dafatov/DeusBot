package ru.demetrious.deus.bot.adapter.output.arting;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.arting.dto.CreateDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.GetDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.PayloadDto;
import ru.demetrious.deus.bot.adapter.output.arting.dto.ResponseDto;
import ru.demetrious.deus.bot.app.api.image.CreateAiImageOutbound;

import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.LF;

@RequiredArgsConstructor
@Slf4j
@Component
public class ArtingAdapter implements CreateAiImageOutbound {
    private static final int MAX_TRIES = 18;
    private static final int SECONDS_DELAY = 10;
    private static final int SUCCESS_CODE = 100000;
    private static final Supplier<String> COOKIE_SUPPLIER = () -> "nlg_id=%s".formatted(randomUUID());

    private final ArtingClient artingClient;

    @SneakyThrows
    @Override
    public Optional<String> createAiImage(String prompt) {
        int index = MAX_TRIES;
        String cookie = COOKIE_SUPPLIER.get();
        ResponseDto<CreateDto> createDtoResponseDto = artingClient.create(cookie, new PayloadDto(prompt));

        log.debug("Created request: {}", createDtoResponseDto);
        if (!createDtoResponseDto.getCode().equals(SUCCESS_CODE)) {
            throw new IllegalStateException(createDtoResponseDto.getMessage());
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (ScheduledExecutorService scheduledExecutor = newSingleThreadScheduledExecutor()) {
            while (--index > 0) {
                List<String> stringList = scheduledExecutor.schedule(() -> {
                    ResponseDto<GetDto> getDtoResponseDto = artingClient.get(cookie, createDtoResponseDto.getData());

                    if (!getDtoResponseDto.getCode().equals(SUCCESS_CODE)) {
                        stringBuilder.append(getDtoResponseDto.getMessage()).append(LF);
                    }

                    log.debug("Get result: {}", getDtoResponseDto);
                    return getDtoResponseDto;
                }, SECONDS_DELAY, SECONDS).get().getData().getOutput();

                if (isNotEmpty(stringList)) {
                    return stringList.stream().findFirst();
                }
            }
        }

        if (!stringBuilder.isEmpty()) {
            throw new IllegalStateException(stringBuilder.toString());
        }

        return empty();
    }
}
