package org.iguana.util.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.iguana.parser.RecognizerStatistics;

import java.io.IOException;

public class RecognizerStatisticsSerializer {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
               .setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
               .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
               .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
               .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        mapper.addMixIn(RecognizerStatistics.class, RecognizerStatisticsMixIn.class);
    }

    public static String serialize(RecognizerStatistics statistics) {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        try {
            return mapper.writer(pp).writeValueAsString(statistics);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RecognizerStatistics deserialize(String s) {
        try {
            return mapper.readValue(s, RecognizerStatistics.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonDeserialize(builder = RecognizerStatistics.Builder.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    abstract static class RecognizerStatisticsMixIn {
    }

}
