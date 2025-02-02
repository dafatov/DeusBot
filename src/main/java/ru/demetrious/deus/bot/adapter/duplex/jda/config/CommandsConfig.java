package ru.demetrious.deus.bot.adapter.duplex.jda.config;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.CommandDataMapper;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CommandsConfig {
    private final JDA jda;
    private final CommandDataMapper commandDataMapper;
    private final List<CommandInbound> commandList;

    @PostConstruct
    public void updateCommand() {
        List<CommandData> commandDataList = commandDataMapper.mapCommand(commandList.stream().map(CommandInbound::getData).toList());

        jda.retrieveCommands()
            .flatMap(CollectionUtils::isNotEmpty, c -> jda.updateCommands())
            .onSuccess(c -> log.info("Global commands are removed"))
            .queue();
        jda.getGuilds().forEach(guild -> guild.updateCommands()
            .addCommands(commandDataList)
            .onSuccess(commandList -> log.info("Init guild({}) commands: {}", guild.getName(), commandList))
            .queue());
    }
}
