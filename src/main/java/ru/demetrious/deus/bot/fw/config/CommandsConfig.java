package ru.demetrious.deus.bot.fw.config;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.demetrious.deus.bot.adapter.inbound.jda.mapper.CommandDataMapper;
import ru.demetrious.deus.bot.app.command.api.Command;

@Slf4j
@RequiredArgsConstructor
@Profile("!local")
@Configuration
public class CommandsConfig {
    private final JDA jda;
    private final CommandDataMapper commandDataMapper;
    private final List<Command> commandList;

    @PostConstruct
    public void updateCommand() {
        jda.updateCommands()
            .addCommands(commandDataMapper.mapCommand(commandList.stream().map(Command::getData).toList()))
            .onSuccess(commandList -> log.info("Init commands: {}", commandList))
            .queue();
    }
}
