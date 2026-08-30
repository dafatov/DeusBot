package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.InstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.HintDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.StateDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.WordDto;

@Getter
@SuperBuilder
public class CodeNamesInstanceDto extends InstanceDto<CodeNamesPlayerDto> {
    private final StateDto state;
    private final Set<WordDto> wordList;
    private final List<HintDto> hintList;
    private final Map<String, VoteDto> voteMap;
}
