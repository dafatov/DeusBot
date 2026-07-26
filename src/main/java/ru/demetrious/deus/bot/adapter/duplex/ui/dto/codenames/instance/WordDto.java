package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance;

import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto.TeamDto;

@Builder
public record WordDto(String text, Color color, RevealDto revealed) {
    public enum Color {
        RED, BLUE, WHITE, BLACK
    }

    public record RevealDto(int order, TeamDto team, int round) {
    }
}
