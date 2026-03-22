package ru.demetrious.deus.bot.adapter.duplex.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.TextInputComponent;

@Mapper
public interface ModalDataMapper {
    default Modal mapModal(ModalData modalData) {
        return Modal.create(modalData.getCustomId(), modalData.getTitle())
            .addComponents(mapComponent(modalData.getComponents()))
            .build();
    }

    List<ModalTopLevelComponent> mapComponent(List<ModalComponent> modalComponents);

    default ModalTopLevelComponent mapComponent(ModalComponent modalComponent) {
        return Label.of(modalComponent.getLabel(), mapTextInput(modalComponent.getTextInput()));
    }

    default TextInput mapTextInput(TextInputComponent textInputComponent) {
        return TextInput.create(textInputComponent.getId(), mapTextInputStyle(textInputComponent.getStyle()))
            .setPlaceholder(textInputComponent.getPlaceholder())
            .setRequired(textInputComponent.isRequired())
            .setValue(textInputComponent.getValue())
            .build();
    }

    TextInputStyle mapTextInputStyle(TextInputComponent.StyleEnum styleEnum);
}
