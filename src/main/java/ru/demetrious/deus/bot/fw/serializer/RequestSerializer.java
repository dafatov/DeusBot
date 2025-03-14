package ru.demetrious.deus.bot.fw.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import ru.demetrious.deus.bot.domain.graphql.Request.RequestInner;
import ru.demetrious.deus.bot.domain.graphql.RequestSerialize;

import static org.apache.commons.collections4.MapUtils.isNotEmpty;

public class RequestSerializer extends StdSerializer<RequestInner> {
    @SuppressWarnings("unused")
    public RequestSerializer() {
        this(null);
    }

    public RequestSerializer(Class<RequestInner> t) {
        super(t);
    }

    @Override
    public void serialize(RequestInner requestInner, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        serialize(stringBuilder, requestInner.getQuery(), "query");
        serialize(stringBuilder, requestInner.getMutation(), "mutation");

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
