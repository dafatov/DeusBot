package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Duration;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;

@Builder
public record TimerDto(
    @JsonFormat(shape = NUMBER) Duration timer,
    @JsonFormat(shape = NUMBER) Duration remaining
) {
}
