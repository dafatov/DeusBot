package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto;

@Getter
@SuperBuilder
public class CodeNamesPlayerDto extends PlayerDto {
    private final TeamDto team;
    private final boolean captain;

    public enum TeamDto {
        SPECTATOR, RED, BLUE
    }
}
