package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.action.Action.checkLocked;

@Builder
public record ChangeTeamAction(Team team, boolean captain) implements Action {
    @Override
    public void perform(GameSession gameSession, String userId, Context ctx) throws ActionException {
        checkLocked(gameSession);

        if (captain && gameSession.getPlayerList().stream().anyMatch(p -> p.getTeam().equals(team) && p.isCaptain())) {
            throw new ActionException("Player can't be a captain cause one already exists");
        }

        gameSession.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Player not found"))
            .setCaptain(captain)
            .setTeam(team);
        gameSession.getVoteMap().remove(userId);
    }
}
