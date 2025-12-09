package ru.demetrious.deus.bot.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.FailableSupplier;

import static java.util.Objects.isNull;

@Log4j2
@UtilityClass
public class DefaultUtils {
    public static <T> T defaultIfException(FailableSupplier<T, Throwable> supplier) {
        return defaultIfException(supplier, null);
    }

    public static <T> T defaultIfException(FailableSupplier<T, Throwable> supplier, T defaultValue) {
        return defaultIfException(supplier, defaultValue, false);
    }

    public static <T> T defaultIfException(FailableSupplier<T, Throwable> supplier, T defaultValue, boolean silent) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (!silent) log.warn("Can't get supplier, return defaultValue={}, cause:", defaultValue, e);
            return defaultValue;
        }
    }

    public static <T> T throwIfException(FailableSupplier<T, Throwable> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            log.warn("Can't get supplier, cause:", e);
            throw new IllegalStateException(e);
        }
    }

    public static Integer defaultIfZero(Integer value) {
        return defaultIfZero(value, null);
    }

    public static Integer defaultIfZero(Integer value, Integer defaultValue) {
        return isNull(value) || value == 0 ? defaultValue : value;
    }
}
