package ru.demetrious.deus.bot.app.api.command;

import ru.demetrious.deus.bot.app.api.interaction.AutocompleteInteractionInbound;
import ru.demetrious.deus.bot.app.api.interaction.ButtonInteractionInbound;
import ru.demetrious.deus.bot.app.api.interaction.ModalInteractionInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.domain.CommandData;

public interface CommandInbound extends SlashCommandInteractionInbound, ButtonInteractionInbound, ModalInteractionInbound, AutocompleteInteractionInbound {
    CommandData getData();

    default String getHint() {
        return "По данной команде нет подсказок. Если Вы это видите, то у Вас есть вопрос " +
            "- адресуйте его администрации сервера/бота, и в дальнейшем эта информация появится здесь";
    }

    default boolean isDefer() {
        return true;
    }

    @Override
    default void onModal() {
        throw new IllegalStateException("CommandInbound onModal is not implemented");
    }

    @Override
    default void onButton() {
        throw new IllegalStateException("CommandInbound onButton is not implemented");
    }

    @Override
    default void onAutocomplete() {
        throw new IllegalStateException("CommandInbound onAutocomplete is not implemented");
    }
}
