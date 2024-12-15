package ru.demetrious.deus.bot.app.api.modal;

import java.util.List;

@FunctionalInterface
public interface GetModalValuesOutbound {
    List<String> getValues();
}
