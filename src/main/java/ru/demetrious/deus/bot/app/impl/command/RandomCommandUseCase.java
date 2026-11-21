package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.GetChoiceOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.RandomCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static ru.demetrious.deus.bot.domain.CommandData.Name.RANDOM;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;

@RequiredArgsConstructor
@Slf4j
@Component
public class RandomCommandUseCase implements RandomCommandInbound {
    private final static String STRING_OPTION_TYPE = "type";

    private final GetChoiceOptionOutbound getChoiceOptionOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(RANDOM)
            .setDescription("Разный набор \"случайных\" значений")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(STRING_OPTION_TYPE)
                    .setDescription("Что сгенерить то?")
                    .setRequired(true)
                    .setChoices(Type.values())
            ));
    }

    @Override
    public void execute() {
        Type type = getChoiceOptionOutbound.getChoiceOption(STRING_OPTION_TYPE, Type.class).orElseThrow();
        String random = switch (type) {
            case BOOLEAN -> BooleanUtils.toString(RandomUtils.secure().randomBoolean(), "Орел", "Решка");
        };
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Рандом!")
            .setDescription(random)));

        notifyOutbound.notify(messageData);
        log.info("Рандом успешно выведен");
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    @Getter
    @RequiredArgsConstructor
    private enum Type implements OptionChoice {
        BOOLEAN("boolean", "Монетка");

        private final String value;
        private final String name;
    }
}
