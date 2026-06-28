package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Builder;

@Builder
public record WordDto(String text, Color color, boolean revealed) {
    public enum Color {
        RED, BLUE, WHITE, BLACK
    }
}
