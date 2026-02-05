package ru.demetrious.deus.bot.app.impl.component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.api.modal.GetModalValuesOutbound;
import ru.demetrious.deus.bot.app.api.modal.ShowModalOutbound;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.TextInputComponent;

import static java.util.Arrays.stream;
import static java.util.Optional.*;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.*;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.DANGER;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.SUCCESS;
import static ru.demetrious.deus.bot.domain.TextInputComponent.StyleEnum.SHORT;

@RequiredArgsConstructor
public class EditorComponent<K, V> implements Component {
    private static final String DATA_DIVIDER = "::";

    private final Map<UUID, Map<K, V>> data;
    private final Consumer<Map<K, V>> saveConsumer;
    private final ShowModalOutbound<?> showModalOutbound;
    private final CommandData.Name commandName;
    private final Map<String, String> editableNames;
    private final GetModalValuesOutbound getModalValuesOutbound;
    private final Function<Pair<String, String>, Pair<K, V>> mapFunction;

    @Getter
    private UUID key;

    public void add(Map<K, V> item) {
        data.put(key = randomUUID(), item);
    }

    public Map<K, V> current() {
        return data.get(key);
    }

    @Override
    public MessageComponent get() {
        return new MessageComponent().setButtons(List.of(
            new ButtonComponent()
                .setStyle(SUCCESS)
                .setId("%s%s%s".formatted(ButtonIdEnum.SAVE.name(), DATA_DIVIDER, key))
                .setLabel(ButtonIdEnum.SAVE.getLabel()),
            new ButtonComponent()
                .setId("%s%s%s".formatted(ButtonIdEnum.EDIT.name(), DATA_DIVIDER, key))
                .setLabel(ButtonIdEnum.EDIT.getLabel()),
            new ButtonComponent()
                .setStyle(DANGER)
                .setId("%s%s%s".formatted(ButtonIdEnum.CANCEL.name(), DATA_DIVIDER, key))
                .setLabel(ButtonIdEnum.CANCEL.getLabel())
        ));
    }

    @Override
    public MessageComponent update(String customId) {
        String[] customIdRaw = customId.split(DATA_DIVIDER);
        key = fromString(customIdRaw[1]);

        if (stream(ButtonIdEnum.values()).map(Enum::name).anyMatch(customIdRaw[0]::equals)) {
            switch (ButtonIdEnum.from(customIdRaw[0])) {
                case CANCEL -> data.remove(key);
                case SAVE -> saveConsumer.accept(data.remove(key));
                case EDIT -> showModalOutbound.showModal(new ModalData()
                .setCustomId("%s%s%s".formatted(commandName.stringify(), DATA_DIVIDER, key))
                .setTitle("Редактирование данных")
                .setComponents(editableNames.keySet().stream()
                    .map(id -> new ModalComponent()
                        .setTextInputs(List.of(new TextInputComponent()
                            .setId(id)
                            .setLabel(editableNames.getOrDefault(id, "<Unknown>"))
                            .setStyle(SHORT)
                            .setRequired(true)
                            .setValue(ofNullable(data.get(key).getOrDefault(mapFunction.apply(Pair.of(id, "0")).getKey(), null))
                                .map(String::valueOf)
                                .orElse(null)))))
                    .toList()));
            }
        }

        return get();
    }

    public MessageComponent edit(String modalId) {
        String[] customIdRaw = modalId.split(DATA_DIVIDER);
        key = fromString(customIdRaw[1]);

        getModalValuesOutbound.getPairs().stream()
            .map(mapFunction)
            .filter(Objects::nonNull)
            .forEach(f -> data.get(key).put(f.getKey(), f.getValue()));
        return get();
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    @Getter
    @RequiredArgsConstructor
    private enum ButtonIdEnum {
        CANCEL("Отменить"),
        EDIT("Изменить"),
        SAVE("Сохранить");

        private final String label;

        public static ButtonIdEnum from(String buttonId) {
            return Enum.valueOf(ButtonIdEnum.class, buttonId);
        }
    }
}
