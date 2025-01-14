package ru.demetrious.deus.bot.adapter.output.freesteam.mapper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.output.freesteam.dto.RssDto.Channel.Item;
import ru.demetrious.deus.bot.domain.freebie.FreebieItem;

@Mapper(imports = {OffsetDateTime.class, DateTimeFormatter.class})
public interface FreebieMapper {
    List<FreebieItem> map(List<Item> item);

    @Mapping(target = "pubDate", expression = "java(OffsetDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    FreebieItem map(Item item);

    default FreebieItem.Category mapCategory(List<String> category) {
        return category.stream()
            .map(FreebieItem.Category::fromValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
