package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.SkipVoteDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.WordVoteDto;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = SkipVoteDto.class, name = "skip"),
    @Type(value = WordVoteDto.class, name = "word")
})
public interface VoteDto {
    @Builder
    record SkipVoteDto() implements VoteDto {
    }

    @Builder
    record WordVoteDto(String word) implements VoteDto {
    }
}
