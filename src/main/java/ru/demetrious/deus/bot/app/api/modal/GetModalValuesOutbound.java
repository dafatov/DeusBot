package ru.demetrious.deus.bot.app.api.modal;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public interface GetModalValuesOutbound {
    List<Pair<String, String>> getPairs();

    List<String> getValues();

    Optional<String> getValue(String id);
}
