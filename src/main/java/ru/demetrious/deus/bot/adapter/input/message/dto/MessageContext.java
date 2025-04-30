package ru.demetrious.deus.bot.adapter.input.message.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageContext(@NotBlank(message = "Не может быть пустым") String channelId, @Valid Message message) {
    public record Message(@NotNull Type type, @NotBlank(message = "Не может быть пустым") String title, String description) {
        public enum Type {
            ERROR, INFO, WARNING
        }
    }
}
