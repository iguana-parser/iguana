package org.iguana.util.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.iguana.parser.ParseStatistics;

import java.io.IOException;

public class ParseStatisticsSerializer {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        mapper.addMixIn(ParseStatistics.class, ParseStatisticsMixIn.class);
    }

    public static String serialize(ParseStatistics statistics) {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        try {
            return mapper.writer(pp).writeValueAsString(statistics);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ParseStatistics deserialize(String s) {
        try {
            return mapper.readValue(s, ParseStatistics.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonDeserialize(builder = ParseStatistics.Builder.class)
    abstract static class ParseStatisticsMixIn {
    }

}
