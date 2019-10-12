package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.util.serialization.JsonSerializer;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class IggyParserTest {

    @Test
    public void test() throws Exception {
        Path grammarPath = Paths.get("test/resources/Grammar.iggy");
        Grammar grammar = IggyParser.getHighLevelGrammar(grammarPath.toAbsolutePath().toString());

        System.out.println(grammar);
        System.out.println(JsonSerializer.serialize(grammar));

        String expectedJson = getFileContent(Paths.get("test/resources/Grammar.json"));
        assertEquals(expectedJson.trim(), JsonSerializer.serialize(grammar).trim());

        Grammar expectedGrammar = JsonSerializer.deserialize(expectedJson, Grammar.class);
        assertEquals(expectedGrammar, grammar);
    }

    private static String getFileContent(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
