package ru.demetrious.deus.bot.adapter.output.anilist.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestSerialize;

import static org.apache.commons.collections4.MapUtils.isNotEmpty;

public class RequestAnilistSerializer extends StdSerializer<RequestAnilist.QueryAnilist> {
    @SuppressWarnings("unused")
    public RequestAnilistSerializer() {
        this(null);
    }

    public RequestAnilistSerializer(Class<RequestAnilist.QueryAnilist> t) {
        super(t);
    }

    @Override
    public void serialize(RequestAnilist.QueryAnilist queryAnilist, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        serialize(stringBuilder, queryAnilist.getQuery(), "query");
        serialize(stringBuilder, queryAnilist.getMutation(), "mutation");

        jsonGenerator.writeRawValue(stringBuilder.toString());
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void serialize(StringBuilder stringBuilder, Map<String, ? extends RequestSerialize> stringMap, String type) {
        if (isNotEmpty(stringMap)) {
            stringBuilder.append("\"").append(type).append("{");
            for (Iterator<? extends Map.Entry<String, ? extends RequestSerialize>> iterator = stringMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, ? extends RequestSerialize> stringEntry = iterator.next();

                stringBuilder.append(stringEntry.getKey())
                    .append(":")
                    .append(stringEntry.getValue().serialize());

                if (iterator.hasNext()) {
                    stringBuilder.append(',');
                }
            }
            stringBuilder.append("}\"");
        }
    }
}
