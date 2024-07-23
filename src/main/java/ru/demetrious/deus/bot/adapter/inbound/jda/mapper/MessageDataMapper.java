package ru.demetrious.deus.bot.adapter.inbound.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.MessageData;

@Mapper
public interface MessageDataMapper {
    default MessageCreateData mapToMessageCreate(MessageData commandData) {
        return new MessageCreateBuilder()
            .setContent(commandData.getContent())
            .setEmbeds(mapEmbed(commandData.getEmbeds()))
            .build();
    }

    List<MessageEmbed> mapEmbed(List<ru.demetrious.deus.bot.domain.MessageEmbed> embeds);

    default MessageEmbed mapEmbed(ru.demetrious.deus.bot.domain.MessageEmbed messageEmbed) {
        return new EmbedBuilder()
            .setTitle(messageEmbed.getTitle())
            .setDescription(messageEmbed.getDescription())
            .setTimestamp(messageEmbed.getTimestamp())
            .setColor(messageEmbed.getColor().getValue())
            .setUrl(messageEmbed.getUrl())
            .setThumbnail(messageEmbed.getThumbnail())
            .build();
    }

    default MessageEditData mapToMessageEdit(MessageData commandData) {
        return new MessageEditBuilder()
            .setContent(commandData.getContent())
            .setEmbeds(mapEmbed(commandData.getEmbeds()))
            .build();
    }
}
