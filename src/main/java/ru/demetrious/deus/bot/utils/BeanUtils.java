package ru.demetrious.deus.bot.utils;

import java.util.List;
import lombok.experimental.UtilityClass;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;

@UtilityClass
public class BeanUtils {
    public static <T extends HasEventOutbound> T b(List<T> beanList) {
        return beanList.stream()
            .filter(HasEventOutbound::hasEvent)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No bean with init event"));
    }
}
