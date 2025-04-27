package ru.demetrious.deus.bot.adapter.input.message;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.adapter.input.message.dto.MessageContext;
import ru.demetrious.deus.bot.app.api.message.PublishMessageInbound;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class MessageAdapter {
    private final PublishMessageInbound publishMessageInbound;

    @PostMapping("/publish")
    public void publishMessage(@Valid @RequestBody MessageContext messageContext) {
        publishMessageInbound.publish(messageContext.channelId(), new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle(messageContext.message().title())
            .setDescription(messageContext.message().description()))));
    }
}
