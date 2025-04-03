package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.ModalDataMapper;
import ru.demetrious.deus.bot.app.api.channel.GetChannelOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.GetAttachmentOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.modal.ShowModalOutbound;
import ru.demetrious.deus.bot.app.api.network.GetLatencyOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserOptionOutbound;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.ModalData;

import static java.util.Optional.ofNullable;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static ru.demetrious.deus.bot.fw.config.spring.SpringConfig.SCOPE_THREAD;

@Slf4j
@RequiredArgsConstructor
@Scope(value = SCOPE_THREAD, proxyMode = TARGET_CLASS)
@Component
public class SlashCommandAdapter extends GenericAdapter<SlashCommandInteractionInbound, SlashCommandInteractionEvent, SlashCommandInteraction>
    implements GetLatencyOutbound, GetStringOptionOutbound, GetAttachmentOptionOutbound, ShowModalOutbound, GetIntegerOptionOutbound, GetChannelOptionOutbound,
    GetUserOptionOutbound {
    private final ModalDataMapper modalDataMapper;

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
        getEvent().replyModal(modalDataMapper.mapModal(modal)).queue();
    }

    @Override
    public Optional<Integer> getIntegerOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsInt);
    }

    @Override
    public CommandData.Name getCommandName() {
        return CommandData.Name.from(getEvent().getName(), getEvent().getSubcommandGroup(), getEvent().getSubcommandName());
    }

    @Override
    public Optional<String> getChannelOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsChannel)
            .map(ISnowflake::getId);
    }

    @Override
    public Optional<String> getUserIdOption(String name) {
        return getOption(name)
            .map(OptionMapping::getAsUser)
            .map(ISnowflake::getId);
    }

    @Override
    protected @NotNull SlashCommandInteraction getInteraction() {
        return getEvent().getInteraction();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Optional<OptionMapping> getOption(String name) {
        return ofNullable(getEvent())
            .map(event -> event.getOption(name));
    }
}
