package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkLocked;

@Builder
public record ChangeTeamAction(Team team, boolean captain) implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx, ActionEvent<CodeNamesAction, CodeNamesPlayer> event) throws ActionException {
        checkLocked(gameSession);

        if (captain && gameSession.getPlayerList().stream().anyMatch(p -> p.getTeam().equals(team) && p.isCaptain())) {
            throw new ActionException("Player can't be a captain cause one already exists");
        }

        player
            .setCaptain(captain)
            .setTeam(team);
        gameSession.getVoteMap().remove(player.getId());
    }
}
