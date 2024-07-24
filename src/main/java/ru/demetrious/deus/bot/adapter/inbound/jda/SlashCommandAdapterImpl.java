package ru.demetrious.deus.bot.adapter.inbound.jda;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.MessageDataMapper;

import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.entities.Message.Attachment;

@Slf4j
public class SlashCommandAdapterImpl extends GenericInteractionAdapterImpl<SlashCommandInteractionEvent, SlashCommandInteraction>
    implements SlashCommandAdapter {
    public SlashCommandAdapterImpl(MessageDataMapper messageDataMapper, SlashCommandInteractionEvent slashCommandInteractionEvent) {
        super(slashCommandInteractionEvent, messageDataMapper);
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
    public Optional<Attachment> getAttachmentOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsAttachment);
    }

    @Override
    public void showModal(Modal modal) {
        event.replyModal(modal).queue();
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
