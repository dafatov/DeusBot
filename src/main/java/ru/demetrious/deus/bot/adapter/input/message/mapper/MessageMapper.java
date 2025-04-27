package ru.demetrious.deus.bot.adapter.input.message.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.input.message.dto.MessageContext;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

@Mapper
public interface MessageMapper {
    default MessageData map(MessageContext messageContext) {
        return new MessageData().setEmbeds(List.of(map(messageContext.message())));
    }

    @Mapping(target = "url", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "footer", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "color", source = "type")
    MessageEmbed map(MessageContext.Message message);
}
