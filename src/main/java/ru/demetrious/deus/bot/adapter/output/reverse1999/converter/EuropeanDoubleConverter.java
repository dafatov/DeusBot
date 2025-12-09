package ru.demetrious.deus.bot.adapter.output.reverse1999.converter;

import com.opencsv.bean.AbstractCsvConverter;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfException;

@Slf4j
public class EuropeanDoubleConverter extends AbstractCsvConverter {
    @Override
    public Double convertToRead(String value) {
        return defaultIfException(() -> ofNullable(value)
            .map(String::trim)
            .map(string -> string.replace(',', '.'))
            .map(Double::parseDouble)
            .orElse(null));
    }
}