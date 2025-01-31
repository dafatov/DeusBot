package ru.demetrious.deus.bot.app.api.modal;

import java.util.List;
import java.util.Optional;

public interface GetModalValuesOutbound {
    List<String> getValues();

    Optional<String> getValue(String id);
}
