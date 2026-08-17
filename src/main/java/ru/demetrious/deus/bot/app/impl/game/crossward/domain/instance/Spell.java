package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.CrosslightSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.CrucifixSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.EchoSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.LonerSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.RadarSpell;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = RadarSpell.class, name = "radar"),
    @Type(value = EchoSpell.class, name = "echo"),
    @Type(value = LonerSpell.class, name = "loner"),
    @Type(value = CrosslightSpell.class, name = "crosslight"),
    @Type(value = CrucifixSpell.class, name = "crucifix"),
})
public interface Spell {
    void use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException;
}
