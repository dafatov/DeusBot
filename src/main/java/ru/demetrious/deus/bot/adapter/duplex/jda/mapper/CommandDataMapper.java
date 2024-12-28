package ru.demetrious.deus.bot.adapter.duplex.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

@Mapper
public interface CommandDataMapper {
    List<net.dv8tion.jda.api.interactions.commands.build.CommandData> mapCommand(List<CommandData> commandData);

    default net.dv8tion.jda.api.interactions.commands.build.CommandData mapCommand(CommandData commandData) {
        return slash(commandData.getName().getValue(), commandData.getDescription())
            .addOptions(mapOption(commandData.getOptions()));
    }

    List<net.dv8tion.jda.api.interactions.commands.build.OptionData> mapOption(List<OptionData> optionData);

    default net.dv8tion.jda.api.interactions.commands.build.OptionData mapOption(OptionData optionData) {
        return new net.dv8tion.jda.api.interactions.commands.build.OptionData(mapOptionType(optionData.getType()), optionData.getName(),
            optionData.getDescription(), optionData.isRequired())
            .addChoices(mapChoice(optionData.getChoices()));
    }

    List<Command.Choice> mapChoice(List<OptionChoice> choices);

    default Command.Choice mapChoice(OptionChoice choice) {
        return new Command.Choice(choice.getName(), choice.getValue());
    }

    net.dv8tion.jda.api.interactions.commands.OptionType mapOptionType(OptionData.Type type);
}
