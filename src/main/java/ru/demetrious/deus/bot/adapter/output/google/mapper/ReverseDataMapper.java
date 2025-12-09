package ru.demetrious.deus.bot.adapter.output.google.mapper;

import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.adapter.output.google.dto.ReverseDataDto;
import ru.demetrious.deus.bot.domain.reverse1999.PullsData;

@Mapper
public interface ReverseDataMapper {
    PullsData map(ReverseDataDto reverseDataDto);
}
