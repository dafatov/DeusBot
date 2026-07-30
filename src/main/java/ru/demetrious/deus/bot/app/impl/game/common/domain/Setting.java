package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesSetting;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesSetting.class, name = CODE_NAMES),
    @Type(value = CrossWardSetting.class, name = CROSS_WARD),
})
public interface Setting {
    String getGame();
}
