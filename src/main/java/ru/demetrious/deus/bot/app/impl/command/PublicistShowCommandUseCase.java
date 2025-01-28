package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.PublicistShowCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.publicist.GetGuildPublicistOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.demetrious.deus.bot.domain.CommandData.Name.PUBLICIST_SHOW;

@RequiredArgsConstructor
@Slf4j
@Component
public class PublicistShowCommandUseCase implements PublicistShowCommandInbound {
    private final GetGuildPublicistOutbound getGuildPublicistOutbound;
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(PUBLICIST_SHOW)
            .setDescription("Показывает текстовый канал как канал для публикаций");
    }

    @Override
    public void execute() {
        Optional<String> channelIdOptional = getGuildPublicistOutbound.getGuildPublicist(getGuildIdOutbound.getGuildId());

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Канал %sустановлен".formatted(channelIdOptional.map(c -> EMPTY).orElse("не ")))
            .setDescription(channelIdOptional
                .map("<#%s>"::formatted)
                .orElse(EMPTY))));
        notifyOutbound.notify(messageData);
        log.info("Каналы публициста успешно выведены");
    }
}
