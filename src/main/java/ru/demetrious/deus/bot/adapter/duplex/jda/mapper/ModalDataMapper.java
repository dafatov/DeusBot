package ru.demetrious.deus.bot.adapter.duplex.jda.mapper;

import java.util.List;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.TextInputComponent;

@Mapper
public interface ModalDataMapper {
    int MAX_COMPONENTS = Modal.MAX_COMPONENTS;

    default Modal mapModal(ModalData modalData) {
        return Modal.create(modalData.getCustomId(), modalData.getTitle())
            .addComponents(mapComponent(modalData.getComponents()))
            .build();
    }

    List<LayoutComponent> mapComponent(List<ModalComponent> modalComponents);

    default LayoutComponent mapComponent(ModalComponent modalComponent) {
        return ActionRow.of(mapTextInput(modalComponent.getTextInputs()));
    }

    List<TextInput> mapTextInput(List<TextInputComponent> textInputComponents);

    default TextInput mapTextInput(TextInputComponent textInputComponent) {
        return TextInput.create(textInputComponent.getId(), textInputComponent.getLabel(), mapTextInputStyle(textInputComponent.getStyle()))
            .setPlaceholder(textInputComponent.getPlaceholder())
            .setRequired(textInputComponent.isRequired())
            .build();
    }

    TextInputStyle mapTextInputStyle(TextInputComponent.StyleEnum styleEnum);
}
