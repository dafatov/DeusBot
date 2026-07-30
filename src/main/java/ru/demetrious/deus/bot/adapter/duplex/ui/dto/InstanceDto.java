package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import java.util.Set;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class InstanceDto<P extends PlayerDto> {
    private final String key;
    private final String hostId;
    private final Set<P> playerList;
    private final TimerDto timer;
}
