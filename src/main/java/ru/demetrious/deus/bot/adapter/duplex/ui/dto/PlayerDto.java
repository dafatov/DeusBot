package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class PlayerDto {
    private final String id;
    private final String name;
    private final String avatar;
    private final boolean disconnected;
}
