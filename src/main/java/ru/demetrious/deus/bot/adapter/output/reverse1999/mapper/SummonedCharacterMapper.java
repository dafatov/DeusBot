package ru.demetrious.deus.bot.adapter.output.reverse1999.mapper;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.output.reverse1999.dto.SummonsDto.Data.Summon;
import ru.demetrious.deus.bot.domain.Pull;

@Mapper
public interface SummonedCharacterMapper {
    List<Pull> map(List<Summon> summonList);

    @Mapping(target = "type", source = "summonType")
    @Mapping(target = "time", source = "createTime")
    @Mapping(target = "summonIdList", source = "gainIds")
    @Mapping(target = "poolId", source = "poolId")
    @Mapping(target = "poolType", source = "poolType")
    Pull map(Summon summon);
}
