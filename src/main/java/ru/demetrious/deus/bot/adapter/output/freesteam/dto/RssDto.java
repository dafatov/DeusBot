package ru.demetrious.deus.bot.adapter.output.freesteam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RssDto {
    private Channel channel;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Channel {
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Item> item = new ArrayList<>();

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class Item {
            private String link;
            @JacksonXmlCData
            private String description;
            private String title;
            @JacksonXmlCData
            @JacksonXmlElementWrapper(useWrapping = false)
            private List<String> category = new ArrayList<>();
            private String pubDate;
        }
    }
}
