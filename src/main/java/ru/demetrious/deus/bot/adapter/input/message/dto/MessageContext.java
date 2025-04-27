package ru.demetrious.deus.bot.adapter.input.message.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record MessageContext(@NotBlank(message = "Не может быть пустым") String channelId, @Valid Message message) {
    public record Message(@NotBlank(message = "Не может быть пустым") String title, String description) {
    }
}
