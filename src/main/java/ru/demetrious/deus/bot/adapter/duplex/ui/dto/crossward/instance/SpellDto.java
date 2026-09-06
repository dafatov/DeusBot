package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.CrosslightSpellDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.CrucifixDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.EchoSpellDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.LonerSpellDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.SpellDto.RadarSpellDto;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = RadarSpellDto.class, name = "radar"),
    @Type(value = EchoSpellDto.class, name = "echo"),
    @Type(value = LonerSpellDto.class, name = "loner"),
    @Type(value = CrosslightSpellDto.class, name = "crosslight"),
    @Type(value = CrucifixDto.class, name = "crucifix"),
})
public interface SpellDto {
    @Builder
    record RadarSpellDto(int x, int y, Character letter) implements SpellDto {
    }

    @Builder
    record EchoSpellDto(int wordId) implements SpellDto {
    }

    @Builder
    record LonerSpellDto(int x, int y) implements SpellDto {
    }

    @Builder
    record CrosslightSpellDto(int wordId) implements SpellDto {
    }

    @Builder
    record CrucifixDto(int x, int y) implements SpellDto {
    }
}
