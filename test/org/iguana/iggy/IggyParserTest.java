package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.util.serialization.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IggyParserTest {

    @Test
    public void test() throws Exception {
        Path grammarPath = Paths.get("src/resources/Iguana.iggy");
        Grammar grammar = IggyParser.getGrammar(grammarPath.toAbsolutePath().toString());

        String expectedJson = getFileContent(Paths.get("src/resources/iggy.json"));
        assertEquals(expectedJson.trim(), JsonSerializer.serialize(grammar).trim());

        Grammar deserializedGrammar = JsonSerializer.deserialize(expectedJson, Grammar.class);
        assertEquals(deserializedGrammar, grammar);
    }

    private static String getFileContent(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
