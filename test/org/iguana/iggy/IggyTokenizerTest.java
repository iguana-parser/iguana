package org.iguana.iggy;

import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.RegularExpressionExamples;
import org.iguana.regex.Token;
import org.iguana.utils.input.Input;
import org.iguana.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IggyTokenizerTest {

    @Test
    public void testIguanaGrammar() throws IOException {
        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        String inputString = FileUtils.readFile(path);

        IguanaTokenizer iguanaTokenizer = IggyTokenizer.getIggyTokenizer();
        iguanaTokenizer.prepare(Input.fromString(inputString), 0);
        StringBuilder sb = new StringBuilder();
        while (iguanaTokenizer.hasNextToken()) {
            Token token = iguanaTokenizer.nextToken();
            sb.append(token.getLexeme());
        }
        assertEquals(inputString, sb.toString());
    }

    @Test
    public void testBadInput() {
        Map<RegularExpression, String> regularExpressions = new HashMap<>();
        RegularExpression id = RegularExpressionExamples.getId();
        RegularExpression floatNumber = RegularExpressionExamples.getFloat();
        regularExpressions.put(floatNumber, "Float");
        regularExpressions.put(id, "Identifier");
        IguanaTokenizer tokenizer = new IguanaTokenizer(regularExpressions);
        Input input = Input.fromString("aaa 123 3.7 bbb 3.7");
        tokenizer.prepare(input, 0);

        List<Token> expected = Arrays.asList(
            new Token(id, "Identifier", input, 0, 3),
            new Token(null, "Error", input, 3, 4),
            new Token(null, "Error", input, 4, 5),
            new Token(null, "Error", input, 5, 6),
            new Token(null, "Error", input, 6, 7),
            new Token(null, "Error", input, 7, 8),
            new Token(floatNumber, "Float", input, 8, 11),
            new Token(null, "Error", input, 11, 12),
            new Token(id, "Identifier", input, 12, 15),
            new Token(null, "Error", input, 15, 16),
            new Token(floatNumber, "Float", input, 16, 19)
        );

        List<Token> actual = new ArrayList<>();
        while (tokenizer.hasNextToken()) {
            actual.add(tokenizer.nextToken());
        }

        assertEquals(expected, actual);
    }
}
