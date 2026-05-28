package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;

@Builder
public record HintDto(String word, Team team, int count, int guessed) {
}
