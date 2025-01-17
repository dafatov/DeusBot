package ru.demetrious.deus.bot.adapter.duplex.jda.mapper;

import java.util.List;
import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Mapper
public interface CommandDataMapper {
    default List<net.dv8tion.jda.api.interactions.commands.build.CommandData> mapCommand(List<CommandData> commandData) {
        return commandData.stream()
            .collect(collectingAndThen(partitioningBy(c -> isAllBlank(c.getName().getSubcommandName(), c.getName().getGroupName())), simpleCommandMap -> {
                Stream<net.dv8tion.jda.api.interactions.commands.build.CommandData> simpleCommandStream = simpleCommandMap.get(true).stream()
                    .map(c -> slash(c.getName().getCommandName(), c.getDescription()).addOptions(mapOption(c.getOptions())));
                Stream<net.dv8tion.jda.api.interactions.commands.build.CommandData> compositeCommandStream = simpleCommandMap.get(false).stream()
                    .collect(collectingAndThen(groupingBy(c -> c.getName().getCommandName()), commandMap -> commandMap.entrySet().stream()
                        .map(commandEntry -> commandEntry.getValue().stream()
                            .collect(collectingAndThen(partitioningBy(c -> isNotBlank(c.getName().getGroupName())), groupCommandMap ->
                                slash(commandEntry.getKey(), "null")
                                    .addSubcommands(getSubcommandDataList(groupCommandMap.get(false)))
                                    .addSubcommandGroups(getSubcommandGroupDataList(groupCommandMap.get(true))))))));

                return concat(simpleCommandStream, compositeCommandStream).collect(toList());
            }));
    }

    List<net.dv8tion.jda.api.interactions.commands.build.OptionData> mapOption(List<OptionData> optionData);

    default net.dv8tion.jda.api.interactions.commands.build.OptionData mapOption(OptionData optionData) {
        return new net.dv8tion.jda.api.interactions.commands.build.OptionData(mapOptionType(optionData.getType()), optionData.getName(),
            optionData.getDescription(), optionData.isRequired(), optionData.isAutoComplete())
            .addChoices(mapChoice(optionData.getChoices()));
    }

    List<Command.Choice> mapChoice(List<OptionChoice> choices);

    default Command.Choice mapChoice(OptionChoice choice) {
        return new Command.Choice(choice.getName(), choice.getValue());
    }

    net.dv8tion.jda.api.interactions.commands.OptionType mapOptionType(OptionData.Type type);

    AutocompleteOption mapAutocompleteOption(AutoCompleteQuery autoCompleteQuery);

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    @NotNull
    private List<SubcommandData> getSubcommandDataList(List<CommandData> groupCommandMap) {
        return groupCommandMap.stream()
            .map(this::createSubcommand)
            .collect(toList());
    }

    @NotNull
    private List<SubcommandGroupData> getSubcommandGroupDataList(List<CommandData> commandData) {
        return commandData.stream()
            .collect(collectingAndThen(groupingBy(c -> c.getName().getGroupName()), cM -> cM.entrySet().stream()
                .map(groupCommandEntry -> createSubcommandGroup(groupCommandEntry.getKey())
                    .addSubcommands(getSubcommandDataList(groupCommandEntry.getValue())))
                .collect(toList())));
    }

    @NotNull
    private SubcommandGroupData createSubcommandGroup(String name) {
        return new SubcommandGroupData(name, "null");
    }

    @NotNull
    private SubcommandData createSubcommand(CommandData commandData1) {
        return new SubcommandData(commandData1.getName().getSubcommandName(), commandData1.getDescription())
            .addOptions(mapOption(commandData1.getOptions()));
    }
}
