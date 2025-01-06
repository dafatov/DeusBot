package ru.demetrious.deus.bot.app.impl.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.message.MessageReceivedInbound;

@RequiredArgsConstructor
@Component
public class MessageReceivedUseCase implements MessageReceivedInbound {
    @Override
    public void execute(String guildId, String userId) {
    }
}
