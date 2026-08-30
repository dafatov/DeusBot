package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting.SCORE_COEFFICIENT_FUNCTION;

@Slf4j
@Builder
public record CrosslightSpell(int wordId) implements Spell {
    @Override
    public boolean use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx, ActionEvent<CrossWardAction, CrossWardPlayer> event) throws ActionException {
        Word word = gameSession.getWords().stream()
            .filter(w -> w.getOrder() == wordId)
            .findFirst()
            .orElseThrow(() -> new ActionException("Word not found"));

        word.getCells().stream()
            .filter(c -> !c.isRevealed() && c.getWords().size() > 1)
            .forEach(c -> c.reveal(SCORE_COEFFICIENT_FUNCTION.apply(player)));
        return false;
    }
}
