package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import lombok.Builder;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Hint;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkPaused;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkTeamMate;

@Builder
public record SetHintGuessedAction(String word, Team team, int guessed) implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx, ActionEvent<CodeNamesAction, CodeNamesPlayer> event) throws ActionException {
        checkPaused(gameSession);
        checkTeamMate(player, team);

        Hint hint = gameSession.getHintList().stream()
            .filter(h -> h.getWord().equals(word) && h.getTeam() == team)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Hint not found"));

        hint.setGuessed(guessed);
    }
}
