package ru.demetrious.deus.bot.adapter.output.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FilesDto(List<File> files) {
    public record File(String id) {
    }
}
