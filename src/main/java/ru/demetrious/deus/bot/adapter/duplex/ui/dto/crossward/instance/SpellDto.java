package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = SpellDto.RadarSpellDto.class, name = "radar"),
})
public interface SpellDto {
    @Builder
    record RadarSpellDto(int x, int y, Character letter) implements SpellDto {
    }
}
