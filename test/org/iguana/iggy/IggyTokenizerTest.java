package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;
import org.iguana.utils.input.Input;
import org.iguana.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IggyTokenizerTest {

    @Test
    public void test() throws IOException {
        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        String inputString = FileUtils.readFile(path);

        IguanaTokenizer iguanaTokenizer = IggyTokenizer.getIggyTokenizer();
        iguanaTokenizer.prepare(Input.fromString(inputString), 0);
        StringBuilder sb = new StringBuilder();
        while (iguanaTokenizer.hasNextToken()) {
            sb.append(iguanaTokenizer.nextToken().getLexeme());
        }
        assertEquals(inputString, sb.toString());
    }
}
