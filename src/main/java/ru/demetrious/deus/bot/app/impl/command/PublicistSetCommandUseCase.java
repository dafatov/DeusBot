package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.channel.GetChannelOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.PublicistSetCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.publicist.SetGuildPublicistOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;

import static ru.demetrious.deus.bot.domain.CommandData.Name.PUBLICIST_SET;
import static ru.demetrious.deus.bot.domain.OptionData.Type.CHANNEL;

@RequiredArgsConstructor
@Component
public class PublicistSetCommandUseCase implements PublicistSetCommandInbound {
    private static final String CHANNEL_OPTION = "channel";

    private final GetChannelOptionOutbound getChannelOptionOutbound;
    private final SetGuildPublicistOutbound setGuildPublicistOutbound;
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(PUBLICIST_SET)
            .setDescription("Устанавливает текстовый канал как канал для публикаций")
            .setOptions(List.of(
                new OptionData()
                    .setType(CHANNEL)
                    .setName(CHANNEL_OPTION)
                    .setDescription("Текстовый канал для публикации")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        String channelId = getChannelOptionOutbound.getChannelOption(CHANNEL_OPTION).orElseThrow();

        setGuildPublicistOutbound.setGuildPublicist(getGuildIdOutbound.getGuildId(), channelId);

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Канал установлен")));
        notifyOutbound.notify(messageData);
    }
}
