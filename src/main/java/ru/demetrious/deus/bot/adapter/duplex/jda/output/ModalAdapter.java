package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.interaction.ModalInteractionInbound;
import ru.demetrious.deus.bot.app.api.modal.GetModalIdOutbound;
import ru.demetrious.deus.bot.app.api.modal.GetModalValuesOutbound;
import ru.demetrious.deus.bot.domain.CommandData;

import static java.util.Optional.ofNullable;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static ru.demetrious.deus.bot.fw.config.spring.SpringConfig.SCOPE_THREAD;

@Slf4j
@RequiredArgsConstructor
@Scope(value = SCOPE_THREAD, proxyMode = TARGET_CLASS)
@Component
public class ModalAdapter extends GenericAdapter<ModalInteractionInbound, ModalInteractionEvent, ModalInteraction> implements
    GetModalValuesOutbound, GetModalIdOutbound {
    @Override
    public List<String> getValues() {
        return getInteraction().getValues().stream()
            .map(ModalMapping::getAsString)
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    @Override
    public Optional<String> getValue(String id) {
        return ofNullable(getInteraction().getValue(id))
            .map(ModalMapping::getAsString)
            .filter(StringUtils::isNotBlank);
    }

    @Override
    protected @NotNull ModalInteraction getInteraction() {
        return getEvent().getInteraction();
    }

    @Override
    public CommandData.Name getCommandName() {
        return getName(getEvent().getModalId().split(DATA_DIVIDER)[0].split(" "));
    }

    @Override
    public String getModalId() {
        return getEvent().getModalId();
    }
}
