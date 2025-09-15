package ru.demetrious.deus.bot.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.FailableSupplier;

@Log4j2
@UtilityClass
public class DefaultUtils {
    public static <T> T throwIfException(FailableSupplier<T, Throwable> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T defaultIfException(FailableSupplier<T, Throwable> supplier) {
        return defaultIfException(supplier, null);
    }

    public static <T> T defaultIfException(FailableSupplier<T, Throwable> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            log.warn("Can't get supplier, return defaultValue={}", defaultValue);
            return defaultValue;
        }
    }
}
