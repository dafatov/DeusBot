package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Builder;

@Builder
public record EventDto<A extends ActionDto>(A action, String issuerId) {
}
