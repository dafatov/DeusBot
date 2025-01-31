package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.AiImageCommandInbound;
import ru.demetrious.deus.bot.app.api.image.CreateAiImageOutbound;
import ru.demetrious.deus.bot.app.api.interaction.DeferOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.modal.GetModalValuesOutbound;
import ru.demetrious.deus.bot.app.api.modal.ShowModalOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.TextInputComponent;

import static ru.demetrious.deus.bot.domain.CommandData.Name.AI_IMAGE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.TextInputComponent.StyleEnum.PARAGRAPH;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Slf4j
@Component
public class AiImageCommandUseCase implements AiImageCommandInbound {
    private static final String PROMPT_FIELD = "prompt";

    private final ShowModalOutbound showModalOutbound;
    private final GetModalValuesOutbound getModalValuesOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final CreateAiImageOutbound createAiImageOutbound;
    private final List<DeferOutbound<?>> deferOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(AI_IMAGE)
            .setDescription("Создать изображение по введенным параметрам");
    }

    @Override
    public boolean isDefer() {
        return false;
    }

    @Override
    public void onModal() {
        b(deferOutbound).defer();

        String prompt = getModalValuesOutbound.getValue(PROMPT_FIELD).orElseThrow();
        Optional<String> aiImageUrlOptional = createAiImageOutbound.createAiImage(prompt);
        MessageData messageData = new MessageData();

        aiImageUrlOptional.ifPresentOrElse(messageData::setContent, () -> messageData.setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Не получилось сгенерировать"))));
        b(notifyOutbound).notify(messageData);
        log.info("Создание изображение завершено с результатом: {}", aiImageUrlOptional);
    }

    @Override
    public void execute() {
        ModalData modalData = new ModalData()
            .setCustomId(AI_IMAGE.stringify())
            .setTitle("Создать изображение")
            .setComponents(List.of(
                new ModalComponent().setTextInputs(List.of(new TextInputComponent()
                    .setId(PROMPT_FIELD)
                    .setStyle(PARAGRAPH)
                    .setLabel("Prompt")
                    .setRequired(true)))
            ));

        showModalOutbound.showModal(modalData);
        log.info("Модальное окно для создания изображения успешно выведено");
    }
}
