package ru.demetrious.deus.bot.adapter.input.message;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.adapter.input.message.dto.NotifyContext;
import ru.demetrious.deus.bot.app.api.message.PublishMessageInbound;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class NotifyAdapter {
    private final PublishMessageInbound publishMessageInbound;

    @PostMapping("/publish")
    public void publishMessage(@RequestBody NotifyContext notifyContext) {
        publishMessageInbound.publish(notifyContext.channelId(), new MessageData()
            .setEmbeds(List.of(new MessageEmbed()
                .setTitle("Сообщение")
                .setDescription("Ответ для <@%s>:\n%s".formatted(notifyContext.userId(), notifyContext.answer())))));
    }
}
