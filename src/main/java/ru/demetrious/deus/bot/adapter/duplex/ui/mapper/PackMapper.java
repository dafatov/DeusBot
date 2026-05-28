package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PackDto;
import ru.demetrious.deus.bot.domain.game.Pack;

@Mapper
public interface PackMapper {
    List<PackDto> map(List<Pack> pack);

    @Mapping(target = "count", expression = "java(pack.getWords().size())")
    PackDto map(Pack pack);
}
