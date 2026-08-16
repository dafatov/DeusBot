package ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.EchoSpell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell.RadarSpell;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/// TODO список способностей на добавление:
/// Открытие все пересечений у выбранного слова
/// Вскрывает случайную букву из тех которые не встречаются повторно в радиусе
/// Вскрывает букву выбранной клетки и ее соседние но завершает ход
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = RadarSpell.class, name = "radar"),
    @Type(value = EchoSpell.class, name = "echo"),
})
public interface Spell {
    void use(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException;
}
