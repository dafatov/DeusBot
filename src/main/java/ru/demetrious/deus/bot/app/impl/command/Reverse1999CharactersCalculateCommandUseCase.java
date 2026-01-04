package ru.demetrious.deus.bot.app.impl.command;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.character.GetReverseDataOutbound;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999CharactersCalculateCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.impl.canvas.ReverseCharacterConsumesCanvas;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterStats;
import ru.demetrious.deus.bot.domain.reverse1999.ReverseData;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound.MAX_CHOICES;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_CALCULATE;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.utils.DefaultUtils.throwIfException;
import static ru.demetrious.deus.bot.utils.JacksonUtils.loadResourceAs;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999CharactersCalculateCommandUseCase implements Reverse1999CharactersCalculateCommandInbound {
    private static final String CHARACTER_OPTION = "character";
    private static final String CURRENT_INSIGHT_OPTION = "current-insight";
    private static final String CURRENT_LEVEL_OPTION = "current-level";
    private static final String CURRENT_RESONANCE_OPTION = "current-resonance";
    private static final String TARGET_INSIGHT_OPTION = "target-insight";
    private static final String TARGET_LEVEL_OPTION = "target-level";
    private static final String TARGET_RESONANCE_OPTION = "target-resonance";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseDataOutbound getReverseDataOutbound;
    private final GetIntegerOptionOutbound getIntegerOptionOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;

    @Value("classpath:reverse1999/consume/level.json")
    private Resource levelConsumeData;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_CHARACTERS_CALCULATE)
            .setDescription("Позволяет посчитать требуемые ресуры на персонажа")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(CHARACTER_OPTION)
                    .setDescription("Персонаж")
                    .setRequired(true)
                    .setAutoComplete(true),
                new OptionData()
                    .setType(INTEGER)
                    .setRequired(true)
                    .setName(CURRENT_INSIGHT_OPTION)
                    .setDescription("Текущий уровень пробуждения персонажа")
                    .setMinValue(0)
                    .setMaxValue(3),
                new OptionData()
                    .setType(INTEGER)
                    .setRequired(true)
                    .setName(CURRENT_LEVEL_OPTION)
                    .setDescription("Текущий уровень персонажа")
                    .setMinValue(1)
                    .setMaxValue(60),
                new OptionData()
                    .setType(INTEGER)
                    .setRequired(true)
                    .setName(CURRENT_RESONANCE_OPTION)
                    .setDescription("Текущий уровень резонанса персонажа")
                    .setMinValue(1)
                    .setMaxValue(15),
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET_INSIGHT_OPTION)
                    .setDescription("Целевой уровень пробуждения персонажа")
                    .setMinValue(0)
                    .setMaxValue(3),
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET_LEVEL_OPTION)
                    .setDescription("Целевой уровень персонажа")
                    .setMinValue(1)
                    .setMaxValue(60),
                new OptionData()
                    .setType(INTEGER)
                    .setName(TARGET_RESONANCE_OPTION)
                    .setDescription("Целевой уровень резонанса персонажа")
                    .setMinValue(1)
                    .setMaxValue(15)
            ));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void onAutocomplete() {
        AutocompleteOption focusedOption = getFocusedOptionOutbound.getFocusedOption();

        if (!CHARACTER_OPTION.equals(focusedOption.getName())) {
            return;
        }

        List<OptionChoice> optionChoiceList = getReverseDataOutbound.getReverseData().getCharacters().entrySet().stream()
            .filter(characterEntry -> containsIgnoreCase(characterEntry.getValue().getName(), focusedOption.getValue()))
            .map(characterEntry -> new OptionChoice()
                .setName(characterEntry.getValue().getName())
                .setValue(String.valueOf(characterEntry.getKey())))
            .limit(MAX_CHOICES)
            .toList();

        replyChoicesOutbound.replyChoices(optionChoiceList);
    }

    @Override
    public void execute() {
        Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> levelConsumes = throwIfException(() -> loadResourceAs(levelConsumeData, new TypeReference<>() {
        }));
        ReverseData reverseData = getReverseDataOutbound.getReverseData();
        CharacterData character = getStringOptionOutbound.getStringOption(CHARACTER_OPTION)
            .map(Integer::valueOf)
            .map(reverseData.getCharacters()::get)
            .orElseThrow();
        CharacterStats current = new CharacterStats(
            getIntegerOptionOutbound.getIntegerOption(CURRENT_INSIGHT_OPTION).orElseThrow(),
            getIntegerOptionOutbound.getIntegerOption(CURRENT_LEVEL_OPTION).orElseThrow(),
            getIntegerOptionOutbound.getIntegerOption(CURRENT_RESONANCE_OPTION).orElseThrow()
        );
        CharacterStats target = new CharacterStats(
            getIntegerOptionOutbound.getIntegerOption(TARGET_INSIGHT_OPTION).orElseGet(() -> getMaxInsight(character)),
            getIntegerOptionOutbound.getIntegerOption(TARGET_LEVEL_OPTION).orElseGet(() -> getMaxLevel(current.insight())),
            getIntegerOptionOutbound.getIntegerOption(TARGET_RESONANCE_OPTION).orElseGet(() -> getMaxResonance(current.insight()))
        );

        Optional<String> validate = validate(character, current, target);
        if (validate.isPresent()) {
            notifyOutbound.notify(new MessageData().setContent(validate.get()));
            return;
        }

        Map<Integer, Integer> resultConsumes = Stream.of(
                mapIntInRange(current.insight(), target.insight(), insight -> {
                    int startLevel = insight == current.insight() ? current.level() + 1 : 2;
                    int endLevel = insight == target.insight() ? target.level() : getMaxLevel(insight);

                    return mapIntInRange(startLevel, endLevel, levelConsumes.get(character.getRarity()).get(insight)::get);
                }),
                mapIntInRange(current.insight() + 1, target.insight(), character.getConsumeData().getInsight()::get),
                mapIntInRange(current.resonance() + 1, target.resonance(), character.getConsumeData().getResonance()::get))
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(toMap(Entry::getKey, Entry::getValue, Integer::sum));
        MessageFile messageFile = new ReverseCharacterConsumesCanvas(
            character,
            Pair.of(current, target),
            resultConsumes,
            reverseData
        ).createFile();
        notifyOutbound.notify(new MessageData().setFiles(List.of(messageFile)));
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private static Optional<String> validate(CharacterData character, CharacterStats current, CharacterStats target) {
        // Проверка на понижение характеристик
        if (target.insight() < current.insight()) {
            return of("Нельзя понижать уровень Прозрения");
        }
        if (target.insight() == current.insight() && target.level() < current.level()) {
            return of("Нельзя понижать уровень персонажа");
        }
        if (target.resonance() < current.resonance()) {
            return of("Нельзя понижать уровень Резонанса");
        }

        // Проверка корреляции Resonance и Insight
        if (target.resonance() >= 2 && target.insight() < 1) {
            return of("Для Резонанса 2+ требуется Прозрение 1");
        }
        if (target.resonance() >= 6 && target.insight() < 2) {
            return of("Для Резонанса 6+ требуется Прозрение 2");
        }
        if (target.resonance() >= 11 && target.insight() < 3) {
            return of("Для Резонанса 11+ требуется Прозрение 3");
        }

        // Проверка максимальных значений
        int maxInsight = getMaxInsight(character);
        if (target.insight() > maxInsight) {
            return of("Целевое Прозрение превышает максимально доступное: " + maxInsight);
        }

        int maxLevel = getMaxLevel(target.insight());
        if (target.level() > maxLevel) {
            return of("Целевой уровень превышает максимальный для Прозрения " + target.insight() + ": " + maxLevel);
        }

        int maxResonance = getMaxResonance(target.insight());
        if (target.resonance() > maxResonance) {
            return of("Целевой Резонанс превышает максимальный для Прозрения " + target.insight() + ": " + maxResonance);
        }

        return empty();
    }

    private static Map<Integer, Integer> mapIntInRange(int startInclusive, int endInclusive, Function<Integer, Map<Integer, Integer>> intMapFunction) {
        return rangeClosed(startInclusive, endInclusive)
            .boxed()
            .map(intMapFunction)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(toMap(Entry::getKey, Entry::getValue, Integer::sum));
    }

    private static Integer getMaxInsight(CharacterData character) {
        return switch (character.getRarity()) {
            case 2, 3, 4 -> 2;
            case 5, 6 -> 3;
            case null, default -> throw new IllegalStateException("Invalid character(id=%s).rarity: %s".formatted(character.getId(), character.getRarity()));
        };
    }

    private static Integer getMaxLevel(Integer targetInsight) {
        return switch (targetInsight) {
            case 0 -> 30;
            case 1 -> 40;
            case 2 -> 50;
            case 3 -> 60;
            case null, default -> throw new IllegalStateException("Unexpected targetInsight: " + targetInsight);
        };
    }

    private static Integer getMaxResonance(Integer targetInsight) {
        return switch (targetInsight) {
            case 0, 1, 2 -> 10;
            case 3 -> 15;
            case null, default -> throw new IllegalStateException("Unexpected targetInsight: " + targetInsight);
        };
    }
}
