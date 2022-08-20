package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.util.serialization.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IggyParserBootstrapTest {

    @Test
    public void test() throws Exception {
        Grammar grammar = IggyParser.getGrammar();

        String expectedJson = getFileContent(Paths.get("src/org/iguana/iggy/gen/iggy.json"));
        assertEquals(expectedJson.trim(), JsonSerializer.serialize(grammar).trim());

        Grammar deserializedGrammar = JsonSerializer.deserialize(expectedJson, Grammar.class);
        assertEquals(deserializedGrammar, grammar);
    }

    private static String getFileContent(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
