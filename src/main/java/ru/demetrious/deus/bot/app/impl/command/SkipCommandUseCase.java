package ru.demetrious.deus.bot.app.impl.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.text.MessageFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.SkipCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.SKIP;

@RequiredArgsConstructor
@Slf4j
@Component
public class SkipCommandUseCase extends PlayerCommand implements SkipCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(SKIP)
            .setDescription("Пропустить текущую композицию");
    }

    @Override
    public void execute() {
        Result<AudioTrack> result = getPlayer().skip();

        switch (result.getStatus()) {
            case IS_NOT_PLAYING -> notifyIsNotPlaying();
            case NOT_SAME_CHANNEL -> notifyIsNotCanConnect();
            case OK -> {
                MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                    .setTitle("Текущая композиция уничтожена")
                    .setDescription(MessageFormat.format("Название того, что играло уже не помню. Прошлое должно остаться в прошлом.\n" +
                        "...Вроде это **{0}**, но уже какая разница?", result.getData().getInfo().title))));

                notifyOutbound.notify(messageData);
                log.info("Композиция была успешно пропущена");
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(result.getStatus()));
        }
    }
}
