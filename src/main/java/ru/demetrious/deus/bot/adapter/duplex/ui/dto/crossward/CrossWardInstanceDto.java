package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.InstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.StateDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.TagDto;

@Getter
@SuperBuilder
public class CrossWardInstanceDto extends InstanceDto<CrossWardPlayerDto> {
    private final StateDto state;
    private final List<PositionCellDto> grid;
    private final Map<Integer, WordDto> words;
    private final List<String> activePlayers;
    private final Map<TagDto, Set<Character>> letterTags;

    @Builder
    public record PositionCellDto(int x, int y, boolean revealed, Character letter, Set<TagDto> tags, Map<OrientationDto, Integer> words) {
    }

    @Builder
    public record WordDto(String background, String border, boolean revealed, Set<CellDto> cells) {
    }

    @Builder
    public record CellDto(int x, int y) {
    }

    public enum OrientationDto {
        HORIZONTAL,
        VERTICAL
    }
}
