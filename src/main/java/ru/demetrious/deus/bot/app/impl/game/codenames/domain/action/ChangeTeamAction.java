package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkLocked;

@Builder
public record ChangeTeamAction(Team team, boolean captain) implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, String userId, CodeNamesActionContext ctx) throws ActionException {
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
