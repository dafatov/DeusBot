package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.embed.GetEmbedOutbound;
import ru.demetrious.deus.bot.app.api.interaction.ButtonInteractionInbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.util.Optional.ofNullable;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static ru.demetrious.deus.bot.fw.config.spring.SpringConfig.SCOPE_THREAD;

@Slf4j
@RequiredArgsConstructor
@Scope(value = SCOPE_THREAD, proxyMode = TARGET_CLASS)
@Component
public class ButtonAdapter extends GenericAdapter<ButtonInteractionInbound, ButtonInteractionEvent, ButtonInteraction> implements
    GetEmbedOutbound, GetCustomIdOutbound {
    @Override
    public MessageEmbed getEmbed(int index) {
        return messageDataMapper.mapEmbed(getEvent().getMessage().getEmbeds().get(index));
    }

    @Override
    public String getCustomId() {
        return getEvent().getComponentId();
    }

    @Override
    public void defer() {
        getEvent().deferEdit().queue();
        log.debug("Deferred command's edit");
    }

    @Override
    public void notify(MessageData messageData, boolean isEphemeral) {
        if (getEvent().isAcknowledged()) {
            getEvent().getHook().editOriginal(messageDataMapper.mapToMessageEdit(messageData)).queue();
        } else {
            getEvent().editMessage(messageDataMapper.mapToMessageEdit(messageData)).queue();
        }
    }

    @Override
    public CommandData.Name getCommandName() {
        return ofNullable(getEvent().getMessage().getInteraction())
            .map(Message.Interaction::getName)
            .map(cN -> cN.split(" "))
            .map(GenericAdapter::getName)
            .orElseThrow();
    }

    @Override
    protected @NotNull ButtonInteraction getInteraction() {
        return getEvent().getInteraction();
    }
}
