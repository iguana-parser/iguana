package org.iguana.iggy;

import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.Token;
import org.iguana.utils.input.Input;
import org.iguana.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

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
            Token token = iguanaTokenizer.nextToken();
            System.out.println(token.getCategory());
            sb.append(token.getLexeme());
        }
        assertEquals(inputString, sb.toString());
    }
}
