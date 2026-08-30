package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = CodeNamesAction.class),
    @Type(value = CrossWardAction.class),
})
public interface Action<P extends Player, G extends Instance<?, P>, C extends ActionContext<G>, A extends Action<P, G, C, A>> {
    String getGame();

    void perform(G gameSession, P player, C ctx, ActionEvent<A, P> event) throws ActionException;
}
