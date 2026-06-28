package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.command.GameCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.CommandData.Name.GAME;

@RequiredArgsConstructor
@Component
public class GameCommandUseCase implements GameCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;

    @Value("${APP_URL}")
    private String appUrl;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(GAME)
            .setDescription("Предоставляет информацию о встроенных играх");
    }

    @Override
    public void execute() {
        notifyOutbound.notify(new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Игры")
            .setDescription("""
                - [Codenames](%s/ui/game/code-names)
                -# Командная игра для двух сторон. Капитаны дают ассоциацию из одного слова и числа (например, «животное — 3»), чтобы намекнуть на нужные ячейки в сетке 5х5. Команды угадывают свои карточки-агенты, но должны избегать карты «убийца» (проигрыш). Побеждают те, кто первыми найдут всех своих шпионов.
                """.formatted(appUrl)))));
    }
}
