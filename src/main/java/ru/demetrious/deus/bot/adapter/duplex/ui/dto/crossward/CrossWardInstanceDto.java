package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.InstanceDto;

@Getter
@SuperBuilder
public class CrossWardInstanceDto extends InstanceDto<CrossWardPlayerDto> {
    private final List<PositionCellDto> grid;

    @Builder
    public record PositionCellDto(int x, int y, Character letter) {
    }
}
