package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound;
import ru.demetrious.deus.bot.app.api.channel.GetChannelIdOutbound;
import ru.demetrious.deus.bot.app.api.command.AiDeusCommandInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotCanConnectOutbound;
import ru.demetrious.deus.bot.app.api.user.GetAuthorIdOutbound;
import ru.demetrious.deus.bot.app.api.voice.AskByVoiceOutbound;
import ru.demetrious.deus.bot.app.api.voice.StartVoiceRecordOutbound;
import ru.demetrious.deus.bot.app.api.voice.StopVoiceRecordOutbound;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.AI_DEUS;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Slf4j
@Component
public class AiDeusCommandUseCase extends PlayerCommand implements AiDeusCommandInbound {
    private static final String START_BUTTON = "start";
    private static final String STOP_BUTTON = "stop";

    private final List<NotifyOutbound<?>> notifyOutbound;
    private final List<IsNotCanConnectOutbound<?>> isNotCanConnectOutbound;
    private final StartVoiceRecordOutbound startVoiceRecordOutbound;
    private final StopVoiceRecordOutbound stopVoiceRecordOutbound;
    private final GetCustomIdOutbound getCustomIdOutbound;
    private final List<GetAuthorIdOutbound<?>> getAuthorIdOutbound;
    private final AskByVoiceOutbound askByVoiceOutbound;
    private final List<GetChannelIdOutbound<?>> getChannelIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(AI_DEUS)
            .setDescription("Ответить на любой вопрос, заданный голосом");
    }

    @Override
    public boolean isDefer() {
        return false;
    }

    @Override
    public void onButton() {
        switch (getCustomIdOutbound.getCustomId()) {
            case START_BUTTON -> startRecord();
            case STOP_BUTTON -> askByRecord();
            default -> throw new IllegalStateException("Unexpected value: " + getCustomIdOutbound.getCustomId());
        }
    }

    @Override
    public void execute() {
        if (b(isNotCanConnectOutbound).isNotCanConnect()) {
            notifyIsNotCanConnect();
            return;
        }

        getPlayer().connect();

        MessageData messageData = new MessageData().setComponents(List.of(new MessageComponent().setButtons(List.of(new ButtonComponent()
            .setId(START_BUTTON)
            .setLabel("Начать запись")))));
        b(notifyOutbound).notify(messageData, true);
        log.info("Успешно создана кнопка для начала записи");
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void startRecord() {
        String userId = b(getAuthorIdOutbound).getAuthorId();
        boolean isStarted = startVoiceRecordOutbound.start(userId);

        if (!isStarted) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Лишнее движение")
                .setDescription("Пользователь <@%s> уже начал запись когда-то ранее".formatted(userId))));
            b(notifyOutbound).notify(messageData);
            log.warn("Запись пользователя уже была начата");
            return;
        }

        MessageData messageData = new MessageData()
            .setEmbeds(List.of(new MessageEmbed().setTitle("Запись <@%s> идет".formatted(userId))))
            .setComponents(List.of(new MessageComponent().setButtons(List.of(new ButtonComponent()
                .setId(STOP_BUTTON)
                .setLabel("Остановить запись")))));
        b(notifyOutbound).notify(messageData);
        log.info("Успешно создана кнопка для остановки записи");
    }

    private void askByRecord() {
        String userId = b(getAuthorIdOutbound).getAuthorId();
        Optional<byte[]> audioOptional = stopVoiceRecordOutbound.stop(userId);

        if (audioOptional.isEmpty()) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Косое движение")
                .setDescription("Пользователь <@%s> еще не начал запись".formatted(userId))));
            b(notifyOutbound).notify(messageData);
            log.warn("Запись пользователя еще не была начата");
            return;
        }

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Запись обрабатывается...")
            .setDescription("**Это может занять некоторое время. Пожалуйста подождите**")));
        b(notifyOutbound).notify(messageData);

        askByVoiceOutbound.ask(audioOptional.get(), userId, b(getChannelIdOutbound).getChannelId().orElseThrow());
        log.info("Успешно отправлена запись на обработку");
    }
}
