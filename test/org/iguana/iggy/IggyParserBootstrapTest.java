package org.iguana.iggy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.iguana.grammar.Grammar;
import org.iguana.iggy.gen.IggyGrammar;
import org.iguana.util.serialization.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IggyParserBootstrapTest {

    @Test
    public void testIggyGrammar() {
        Grammar grammar = IggyParserUtils.fromIggyGrammarPath("src/resources/Iguana.iggy");

        assertEquals(grammar, IggyGrammar.getGrammar());
    }

    @Test
    public void testJsonGrammar() throws Exception {
        Grammar grammar = IggyGrammar.getGrammar();

        String expectedJson = getFileContent(Paths.get("src/resources/iggy.json"));
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> expected = (Map<String, Object>)(om.readValue(expectedJson, Map.class));
        Map<String, Object> actual = (Map<String, Object>)(om.readValue(JsonSerializer.serialize(grammar), Map.class));

        assertEquals(expected, actual);

        Grammar deserializedGrammar = JsonSerializer.deserialize(expectedJson, Grammar.class);
        assertEquals(deserializedGrammar, grammar);
    }

    private static String getFileContent(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
