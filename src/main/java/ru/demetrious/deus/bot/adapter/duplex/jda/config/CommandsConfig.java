package ru.demetrious.deus.bot.adapter.duplex.jda.config;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.CommandDataMapper;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;

@Slf4j
@RequiredArgsConstructor
@Profile("!local")
@Configuration
public class CommandsConfig {
    private final JDA jda;
    private final CommandDataMapper commandDataMapper;
    private final List<CommandInbound> commandList;

    @PostConstruct
    public void updateCommand() {
        jda.updateCommands()
            .addCommands(commandDataMapper.mapCommand(commandList.stream().map(CommandInbound::getData).toList()))
            .onSuccess(commandList -> log.info("Init commands: {}", commandList))
            .queue();
    }
}
