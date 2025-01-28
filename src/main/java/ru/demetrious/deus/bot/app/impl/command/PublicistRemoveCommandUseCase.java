package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.PublicistRemoveCommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.publicist.RemoveGuildPublicistOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.PUBLICIST_REMOVE;

@RequiredArgsConstructor
@Slf4j
@Component
public class PublicistRemoveCommandUseCase implements PublicistRemoveCommandInbound {
    private final RemoveGuildPublicistOutbound removeGuildPublicistOutbound;
    private final GetGuildIdOutbound<SlashCommandInteractionInbound> getGuildIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(PUBLICIST_REMOVE)
            .setDescription("Удаляет текстовый канал как канал для публикаций");
    }

    @Override
    public void execute() {
        removeGuildPublicistOutbound.removeGuildPublicist(getGuildIdOutbound.getGuildId());

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Канал удален")));
        notifyOutbound.notify(messageData);
        log.info("Канал публициста успешно удален");
    }
}
