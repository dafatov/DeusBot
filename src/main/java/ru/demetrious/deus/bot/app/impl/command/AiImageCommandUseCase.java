package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.AiImageCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.image.CreateAiImageOutbound;
import ru.demetrious.deus.bot.app.api.interaction.DeferOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.modal.GetModalIdOutbound;
import ru.demetrious.deus.bot.app.api.modal.GetModalValuesOutbound;
import ru.demetrious.deus.bot.app.api.modal.ShowModalOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.ModalComponent;
import ru.demetrious.deus.bot.domain.ModalData;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.TextInputComponent;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.LF;
import static ru.demetrious.deus.bot.app.api.modal.GetModalIdOutbound.DATA_DIVIDER;
import static ru.demetrious.deus.bot.domain.CommandData.Name.AI_IMAGE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.INTEGER;
import static ru.demetrious.deus.bot.domain.TextInputComponent.StyleEnum.PARAGRAPH;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Slf4j
@Component
public class AiImageCommandUseCase implements AiImageCommandInbound {
    private static final String PROMPT_FIELD = "prompt";
    private static final String COUNT_OPTION = "count";
    private static final int DEFAULT_COUNT = 1;

    private final ShowModalOutbound showModalOutbound;
    private final GetModalValuesOutbound getModalValuesOutbound;
    private final GetIntegerOptionOutbound getIntegerOptionOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final CreateAiImageOutbound createAiImageOutbound;
    private final List<DeferOutbound<?>> deferOutbound;
    private final GetModalIdOutbound getModalIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(AI_IMAGE)
            .setDescription("Создать изображение по введенным параметрам")
            .setOptions(List.of(
                new OptionData()
                    .setType(INTEGER)
                    .setName(COUNT_OPTION)
                    .setDescription("Количество сгенерированных изображений. По умолчанию: %s".formatted(DEFAULT_COUNT))
                    .setMinValue(1)
                    .setMaxValue(10)
            ));
    }

    @Override
    public boolean isDefer() {
        return false;
    }

    @Override
    public void onModal() {
        b(deferOutbound).defer();

        int count = parseInt(getModalIdOutbound.getModalId().split(DATA_DIVIDER)[1]);
        String prompt = getModalValuesOutbound.getValue(PROMPT_FIELD).orElseThrow();
        List<String> aiImageUrlList;

        try (ExecutorService executorService = newCachedThreadPool()) {
            List<CompletableFuture<Optional<String>>> futureList = rangeClosed(1, count)
                .mapToObj(i -> supplyAsync(() -> createAiImageOutbound.createAiImage(prompt), executorService))
                .toList();

            log.debug("Creating {} images...", futureList.size());
            allOf(futureList.toArray(CompletableFuture[]::new)).join();
            aiImageUrlList = futureList.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .toList();
        }

        MessageData messageData = new MessageData();

        if (isEmpty(aiImageUrlList)) {
            messageData.setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Не получилось сгенерировать")));
        } else {
            messageData.setContent(join(LF, aiImageUrlList));
        }

        b(notifyOutbound).notify(messageData);
        log.info("Создание изображений завершено с результатом: {}", aiImageUrlList);
    }

    @Override
    public void execute() {
        Integer count = getIntegerOptionOutbound.getIntegerOption(COUNT_OPTION).orElse(DEFAULT_COUNT);
        ModalData modalData = new ModalData()
            .setCustomId(("%s%s%d").formatted(AI_IMAGE.stringify(), DATA_DIVIDER, count))
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
