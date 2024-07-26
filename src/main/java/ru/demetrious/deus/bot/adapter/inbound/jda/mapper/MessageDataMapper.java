package ru.demetrious.deus.bot.adapter.inbound.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.time.Instant.from;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.values;

@Mapper
public interface MessageDataMapper {
    default MessageCreateData mapToMessageCreate(MessageData commandData) {
        return new MessageCreateBuilder()
            .setContent(commandData.getContent())
            .setEmbeds(mapEmbed(commandData.getEmbeds()))
            .setComponents(mapComponent(commandData.getComponents()))
            .build();
    }

    List<LayoutComponent> mapComponent(List<MessageComponent> components);

    default LayoutComponent mapComponent(MessageComponent messageComponent) {
        return ActionRow.of(mapButton(messageComponent.getButtons()));
    }

    List<net.dv8tion.jda.api.interactions.components.ItemComponent> mapButton(List<ButtonComponent> items);

    default net.dv8tion.jda.api.interactions.components.ItemComponent mapButton(ButtonComponent buttonComponent) {
        return Button.of(mapButtonStyle(buttonComponent.getStyle()), buttonComponent.getId(), mapEmoji(buttonComponent.getEmoji()))
            .withDisabled(buttonComponent.isDisabled());
    }

    ButtonStyle mapButtonStyle(ButtonComponent.StyleEnum style);

    default Emoji mapEmoji(ButtonComponent.EmojiEnum emoji) {
        return Emoji.fromFormatted(emoji.getValue());
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
            .setFooter(messageEmbed.getFooter())
            .build();
    }

    default ru.demetrious.deus.bot.domain.MessageEmbed mapEmbed(MessageEmbed messageEmbed) {
        return new ru.demetrious.deus.bot.domain.MessageEmbed()
            .setTitle(messageEmbed.getTitle())
            .setDescription(messageEmbed.getDescription())
            .setTimestamp(from(requireNonNull(messageEmbed.getTimestamp())))
            .setColor(stream(values())
                .filter(colorEnum -> colorEnum.getValue().equals(messageEmbed.getColor()))
                .findFirst()
                .orElseThrow())
            .setUrl(messageEmbed.getUrl())
            .setThumbnail(ofNullable(messageEmbed.getThumbnail())
                .map(MessageEmbed.Thumbnail::getUrl)
                .orElse(null))
            .setFooter(ofNullable(messageEmbed.getFooter())
                .map(MessageEmbed.Footer::getText)
                .orElse(null));
    }

    default MessageEditData mapToMessageEdit(MessageData commandData) {
        return new MessageEditBuilder()
            .setContent(commandData.getContent())
            .setEmbeds(mapEmbed(commandData.getEmbeds()))
            .setComponents(mapComponent(commandData.getComponents()))
            .build();
    }

    default AttachmentOption mapAttachmentOption(Message.Attachment attachment) {
        return new AttachmentOption()
            .setUrl(attachment.getUrl())
            .setFileName(attachment.getFileName());
    }
}
