package ru.demetrious.deus.bot.app.api.modal;

@FunctionalInterface
public interface GetModalIdOutbound {
    String DATA_DIVIDER = "::";

    String getModalId();
}
