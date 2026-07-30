package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesSettingDto;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesSettingDto.class, name = CODE_NAMES),
})
public interface SettingDto {
}
