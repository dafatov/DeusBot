package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance;

import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto.TeamDto;

@Builder
public record HintDto(String word, TeamDto team, int count, int guessed) {
}
