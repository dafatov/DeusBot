package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto.TeamDto;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;

@Builder
public record GameSessionDto(
    String key,
    String hostId,
    StateDto state,
    Set<PlayerDto> playerList,
    Set<WordDto> wordList,
    List<HintDto> hintList,
    Map<String, VoteDto> voteMap
) {
    @Builder
    public record StateDto(
        Map<TeamDto, Integer> score,
        PhaseDto phase,
        TeamDto team,
        @JsonFormat(shape = NUMBER) Duration timer,
        @JsonFormat(shape = NUMBER) Duration remaining,
        boolean locked
    ) {
        public enum PhaseDto {
            WAITING, HINTING, GUESSING, FINISHED
        }
    }
}
