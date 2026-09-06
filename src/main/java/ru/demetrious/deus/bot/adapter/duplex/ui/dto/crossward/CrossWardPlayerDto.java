package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto;

@Getter
@SuperBuilder
public class CrossWardPlayerDto extends PlayerDto {
    private final boolean spectator;
    private final String color;
    private int score;
}
