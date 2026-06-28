package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Builder;

@Builder
public record PlayerDto(String id, TeamDto team, boolean captain, String name, String avatar, boolean disconnected) {
    public enum TeamDto {
        SPECTATOR, RED, BLUE
    }
}
