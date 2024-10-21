package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.apache.commons.lang3.StringUtils;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.ModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

@Slf4j
public class ModalAdapterImpl extends GenericInteractionAdapterImpl<ModalInteractionEvent, ModalInteraction> implements ModalAdapter {
    public ModalAdapterImpl(MessageDataMapper messageDataMapper, ModalInteractionEvent modalInteractionEvent) {
        super(modalInteractionEvent, messageDataMapper);
    }

    @Override
    public List<String> getValues() {
        return getInteraction().getValues().stream()
            .map(ModalMapping::getAsString)
            .filter(StringUtils::isNotBlank)
            .toList();
    }
}
