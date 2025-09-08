package ru.demetrious.deus.bot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@UtilityClass
public class JacksonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(WRITE_DATES_AS_TIMESTAMPS);
    private static final ObjectMapper XML_MAPPER = new XmlMapper()
        .registerModule(new JavaTimeModule())
        .disable(WRITE_DATES_AS_TIMESTAMPS);

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

    public static ObjectMapper getXmlMapper() {
        return XML_MAPPER;
    }

    public static <T> String writeValueAsString(T t) {
        try {
            return getMapper().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
