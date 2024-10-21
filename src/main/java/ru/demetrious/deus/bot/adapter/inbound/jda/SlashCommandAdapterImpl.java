package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.ModalDataMapper;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.ModalData;

import static java.util.Optional.ofNullable;

@Slf4j
public class SlashCommandAdapterImpl extends GenericInteractionAdapterImpl<SlashCommandInteractionEvent, SlashCommandInteraction>
    implements SlashCommandAdapter {
    private final ModalDataMapper modalDataMapper;

    public SlashCommandAdapterImpl(MessageDataMapper messageDataMapper, SlashCommandInteractionEvent slashCommandInteractionEvent,
                                   ModalDataMapper modalDataMapper) {
        super(slashCommandInteractionEvent, messageDataMapper);
        this.modalDataMapper = modalDataMapper;
    }

    @Override
    public String getLatency() {
        return "?";
    }

    @Override
    public Optional<String> getStringOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsString);
    }

    @Override
    public Optional<AttachmentOption> getAttachmentOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsAttachment)
            .map(messageDataMapper::mapAttachmentOption);
    }

    @Override
    public void showModal(ModalData modal) {
        event.replyModal(modalDataMapper.mapModal(modal)).queue();
    }

    @Override
    public Optional<Integer> getIntegerOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsInt);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Optional<OptionMapping> getOption(String name) {
        return ofNullable(event.getOption(name));
    }
}
