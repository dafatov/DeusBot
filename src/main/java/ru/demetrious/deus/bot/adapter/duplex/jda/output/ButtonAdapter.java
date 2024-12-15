package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.interaction.ButtonInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.UpdateMessageOutbound;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static ru.demetrious.deus.bot.fw.config.SpringConfig.SCOPE_THREAD;

@Slf4j
@RequiredArgsConstructor
@Scope(value = SCOPE_THREAD, proxyMode = TARGET_CLASS)
@Component
public class ButtonAdapter extends GenericAdapter<ButtonInteractionInbound, ButtonInteractionEvent, ButtonInteraction> implements
    GetEmbedOutbound, UpdateMessageOutbound, GetCustomIdOutbound {
    @Override
    public MessageEmbed getEmbed(int index) {
        return messageDataMapper.mapEmbed(event.getMessage().getEmbeds().get(index));
    }

    @Override
    public void update(MessageData messageData) {
        event.editMessage(messageDataMapper.mapToMessageEdit(messageData)).queue();
    }

    @Override
    public String getCustomId() {
        return event.getComponentId();
    }

    @Override
    protected @NotNull ButtonInteraction getInteraction() {
        return event.getInteraction();
    }
}
