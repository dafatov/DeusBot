package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;

@Getter
@Builder
public class StateDto {
    @JsonFormat(shape = NUMBER)
    private final Instant startedAt;
    private final PhaseDto phase;
    private final boolean locked;
    private final String currentPlayer;
    private final int currentEnergy;

    public enum PhaseDto {
        WAITING, PLAYING, FINISHED
    }
}
