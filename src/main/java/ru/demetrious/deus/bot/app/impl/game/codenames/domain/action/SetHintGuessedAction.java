package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Hint;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkTeamMate;

@Builder
public record SetHintGuessedAction(String word, Team team, int guessed) implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkPaused(gameSession);
        checkTeamMate(gameSession, userId, team);

        Hint hint = gameSession.getHintList().stream()
            .filter(h -> h.getWord().equals(word) && h.getTeam() == team)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Hint not found"));

        hint.setGuessed(guessed);
    }
}
