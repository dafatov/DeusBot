package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.FirstCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.util.Optional.of;
import static ru.demetrious.deus.bot.domain.CommandData.Name.FIRST;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;

@Slf4j
@Component
public class FirstCommandUseCase extends MoveCommandUseCase implements FirstCommandInbound {
    public FirstCommandUseCase(GetIntegerOptionOutbound getIntegerOptionOutbound, NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound) {
        super(getIntegerOptionOutbound, notifyOutbound);
    }

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(FIRST)
            .setDescription("Переместить композицию с места в очереди на первую")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET)
                    .setDescription(TARGET_DESCRIPTION)
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        move(of(0));
    }
}
