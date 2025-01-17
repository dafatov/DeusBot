package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.MessageDataMapper;
import ru.demetrious.deus.bot.app.api.message.PublishMessageOutbound;
import ru.demetrious.deus.bot.domain.MessageData;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class MessagePublishAdapter implements PublishMessageOutbound {
    private final JDA jda;
    private final MessageDataMapper messageDataMapper;

    @Override
    public void publish(String channelId, MessageData messageData) {
        ofNullable(jda.getChannelById(MessageChannel.class, channelId))
            .map(textChannel -> textChannel.sendMessage(messageDataMapper.mapToMessageCreate(messageData)))
            .map(RestAction::complete)
            .filter(message -> message.getChannel() instanceof NewsChannel)
            .map(Message::crosspost)
            .ifPresent(RestAction::queue);
    }
}
