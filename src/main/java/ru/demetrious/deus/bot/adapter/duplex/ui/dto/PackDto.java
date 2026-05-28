package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Builder;

@Builder
public record PackDto(Long id, String name, int count) {
}
