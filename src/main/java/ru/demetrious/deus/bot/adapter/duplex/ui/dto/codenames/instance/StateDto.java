package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto.TeamDto;

@Getter
@Builder
public class StateDto {
    private final Map<TeamDto, Integer> score;
    private final PhaseDto phase;
    private final TeamDto team;
    private final boolean locked;

    public enum PhaseDto {
        WAITING, HINTING, GUESSING, FINISHED
    }
}
