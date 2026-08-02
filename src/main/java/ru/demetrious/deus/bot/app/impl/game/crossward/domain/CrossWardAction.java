package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.StartGameAction;

import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonSubTypes({
    @Type(value = GetStateAction.class, name = "get_state"),
    @Type(value = StartGameAction.class, name = "start_game"),
})
public interface CrossWardAction extends Action<CrossWardSetting, CrossWardPlayer, CrossWardInstance, CrossWardActionContext> {
    @Override
    default String getGame() {
        return CROSS_WARD;
    }
}
