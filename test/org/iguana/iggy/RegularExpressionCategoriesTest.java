package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.iggy.gen.IggyParser;
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

public class RegularExpressionCategoriesTest {

    @Test
    public void testIggyGrammar() throws IOException {
        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        String inputString = FileUtils.readFile(path);

        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(RegularExpressionCategories.getCategories(IggyParser.getGrammar()));
        iguanaTokenizer.prepare(Input.fromString(inputString), 0);
        StringBuilder sb = new StringBuilder();
        while (iguanaTokenizer.hasNextToken()) {
            Token token = iguanaTokenizer.nextToken();
            sb.append(token.getLexeme());
        }
        assertEquals(inputString, sb.toString());
    }

    @Test
    public void testOrderOfRegularExpressions() {
        Grammar grammar = IggyParser.getGrammar();
        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(RegularExpressionCategories.getCategories(grammar));
        Input input = Input.fromString("var a = 1");
        iguanaTokenizer.prepare(input, 0);

        RuntimeGrammar runtimeGrammar = grammar.toRuntimeGrammar();
        RegularExpression id = runtimeGrammar.getRegularExpressions().get("LetterOrDigits");
        RegularExpression number = runtimeGrammar.getRegularExpressions().get("Number");
        RegularExpression whitespace = runtimeGrammar.getRegularExpressions().get("WhiteSpace");
        RegularExpression equals = runtimeGrammar.getLiterals().get("=");
        RegularExpression var = runtimeGrammar.getLiterals().get("var");

        List<Token> expected = Arrays.asList(
            new Token(var, "Keyword", input, 0, 3),
            new Token(whitespace, "WhiteSpace", input, 3, 4),
            new Token(id, "Identifier", input, 4, 5),
            new Token(whitespace, "WhiteSpace", input, 5, 6),
            new Token(equals, "=", input, 6, 7),
            new Token(whitespace, "WhiteSpace", input, 7, 8),
            new Token(number, "Number", input, 8, 9)
        );

        List<Token> actual = new ArrayList<>();
        while (iguanaTokenizer.hasNextToken()) {
            Token token = iguanaTokenizer.nextToken();
            actual.add(token);
        }

        assertEquals(expected, actual);
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
