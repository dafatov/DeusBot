package ru.demetrious.deus.bot.adapter.input.message;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demetrious.deus.bot.adapter.input.message.dto.MessageContext;
import ru.demetrious.deus.bot.adapter.input.message.mapper.MessageMapper;
import ru.demetrious.deus.bot.app.api.message.PublishMessageInbound;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class MessageAdapter {
    private final PublishMessageInbound publishMessageInbound;
    private final MessageMapper messageMapper;

    @PostMapping("/publish")
    public void publishMessage(@Valid @RequestBody MessageContext messageContext) {
        publishMessageInbound.publish(messageContext.channelId(), messageMapper.map(messageContext));
    }
}
