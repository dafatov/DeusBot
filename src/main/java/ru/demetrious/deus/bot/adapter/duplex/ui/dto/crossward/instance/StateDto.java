package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StateDto {
    private final PhaseDto phase;
    private final boolean locked;
    private final String currentPlayer;

    public enum PhaseDto {
        WAITING, PLAYING, FINISHED
    }
}
