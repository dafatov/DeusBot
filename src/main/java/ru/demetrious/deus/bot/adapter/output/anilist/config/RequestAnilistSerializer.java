package ru.demetrious.deus.bot.adapter.output.anilist.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.MutationAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.RequestAnilist;

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

        if (isNotEmpty(queryAnilist.getQuery())) {
            stringBuilder.append("\"query{");
            for (Iterator<? extends Map.Entry<String, ? extends QueryAnilist>> iterator = queryAnilist.getQuery().entrySet().iterator();
                iterator.hasNext(); ) {
                Map.Entry<String, ? extends QueryAnilist> stringEntry = iterator.next();
                stringBuilder.append(stringEntry.getKey())
                    .append(":")
                    .append(stringEntry.getValue().serialize());

                if (iterator.hasNext()) {
                    stringBuilder.append(',');
                }
            }
            stringBuilder.append("}\"");
        }

        if (isNotEmpty(queryAnilist.getMutation())) {
            stringBuilder.append("\"mutation{");
            for (Iterator<? extends Map.Entry<String, ? extends MutationAnilist>> iterator = queryAnilist.getMutation().entrySet().iterator();
                iterator.hasNext(); ) {
                Map.Entry<String, ? extends MutationAnilist> stringEntry = iterator.next();
                stringBuilder.append(stringEntry.getKey())
                    .append(":")
                    .append(stringEntry.getValue().serialize());

                if (iterator.hasNext()) {
                    stringBuilder.append(',');
                }
            }
            stringBuilder.append("}\"");
        }

        jsonGenerator.writeRawValue(stringBuilder.toString());
    }
}
