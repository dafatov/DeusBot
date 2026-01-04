package ru.demetrious.deus.bot.app.api.button;

@FunctionalInterface
public interface GetCustomIdOutbound {
    String DATA_DIVIDER = "::";

    String getCustomId();
}
