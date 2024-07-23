package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang3.StringUtils;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.OnModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.domain.MessageData;

import static net.dv8tion.jda.api.entities.Message.MessageFlag.LOADING;

@Slf4j
@RequiredArgsConstructor
public class OnModalAdapterImpl implements OnModalAdapter {
    private final MessageDataMapper messageDataMapper;
    private final ModalInteractionEvent event;

    @Override
    public void notify(MessageData messageData) {
        MessageCreateData content = messageDataMapper.mapToMessageCreate(messageData);

        try {
            if (event.isAcknowledged() && isDeferred()) {
                event.getHook().editOriginal(messageDataMapper.mapToMessageEdit(messageData)).queue();
            } else if (event.isAcknowledged() && !isDeferred()) {
                event.getHook().sendMessage(content).queue();
            } else {
                event.reply(content).queue();
            }
        } catch (Exception e) {
            log.warn("Cannot reply onModal interaction", e);
            event.getChannel().sendMessage(content).queue();
        }
    }

    @Override
    public List<String> getValues() {
        return event.getInteraction().getValues().stream()
            .map(ModalMapping::getAsString)
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Boolean isDeferred() throws InterruptedException, ExecutionException {
        return event.getInteraction().getHook().retrieveOriginal().submit()
            .thenApply(Message::getFlags)
            .thenApply(messageFlags -> messageFlags.contains(LOADING))
            .get();
    }
}
