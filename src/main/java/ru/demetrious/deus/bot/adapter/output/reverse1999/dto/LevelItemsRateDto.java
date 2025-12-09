package ru.demetrious.deus.bot.adapter.output.reverse1999.dto;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.apache.commons.collections4.MultiValuedMap;
import ru.demetrious.deus.bot.adapter.output.reverse1999.converter.EuropeanDoubleConverter;

@Data
public class LevelItemsRateDto {
    @CsvBindByName(column = "id")
    private Integer id;
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindAndJoinByName(column = "\\d+", elementType = Double.class, converter = EuropeanDoubleConverter.class)
    private MultiValuedMap<String, Double> joinedFields;
}
