package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.LoopCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.LOOP;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoopCommandUseCase extends PlayerCommand implements LoopCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(LOOP)
            .setDescription("Зациклить/отциклить проигрывание композиции");
    }

    @Override
    public void execute() {
        Result<Boolean> result = getPlayer().loop();

        switch (result.getStatus()) {
            case IS_NOT_PLAYING -> notifyIsNotPlaying();
            case NOT_SAME_CHANNEL -> notifyIsNotCanConnect();
            case IS_PLAYING_LIVE -> notifyIsLive();
            case OK -> {
                MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                    .setTitle("Проигрывание " + (result.getData() ? "зациклена" : "отциклена"))
                    .setDescription(result.getData() ? "オラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオ" +
                        "ラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラオラ..."
                        : "しーん...")));

                notifyOutbound.notify(messageData);
                log.info("Композиция была успешна " + (result.getData() ? "зациклена" : "отциклена"));
            }
            default -> throw new IllegalArgumentException("Unexpected status player operation: %s".formatted(result.getStatus()));
        }
    }
}
