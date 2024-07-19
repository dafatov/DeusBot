package ru.demetrious.deus.bot.adapter.inbound.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.CommandData;

@Mapper
public interface CommandDataMapper {
    List<net.dv8tion.jda.api.interactions.commands.build.CommandData> mapCommand(List<CommandData> commandData);

    default net.dv8tion.jda.api.interactions.commands.build.CommandData mapCommand(CommandData commandData) {
        return Commands.slash(commandData.getName(), commandData.getDescription());
    }
}
