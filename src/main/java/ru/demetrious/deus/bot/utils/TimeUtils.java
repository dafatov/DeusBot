package ru.demetrious.deus.bot.utils;

import java.time.ZoneId;
import lombok.experimental.UtilityClass;

import static java.time.ZoneId.of;

@UtilityClass
public class TimeUtils {
    public static final ZoneId ZONE_ID = of("Europe/Moscow");
}
