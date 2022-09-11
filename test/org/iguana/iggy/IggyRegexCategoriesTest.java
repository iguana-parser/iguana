package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.iggy.gen.IggyGrammar;
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

public class IggyRegexCategoriesTest {

    @Test
    public void testIggyGrammar() throws IOException {
        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        String inputString = FileUtils.readFile(path);

        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(IggyRegexCategories.getCategories());
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
        Grammar grammar = IggyGrammar.getGrammar();
        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(IggyRegexCategories.getCategories());
        Input input = Input.fromString(
            "// this is a comment\n" +
            "     " +
            "var a = 1");
        iguanaTokenizer.prepare(input, 0);

        RuntimeGrammar runtimeGrammar = grammar.toRuntimeGrammar();
        RegularExpression singleLineComment = runtimeGrammar.getRegularExpressions().get("SingleLineComment");
        RegularExpression id = runtimeGrammar.getRegularExpressions().get("LetterOrDigits");
        RegularExpression number = runtimeGrammar.getRegularExpressions().get("Number");
        RegularExpression whitespace = runtimeGrammar.getRegularExpressions().get("WhiteSpace");
        RegularExpression equals = runtimeGrammar.getLiterals().get("=");
        RegularExpression var = runtimeGrammar.getLiterals().get("var");

        List<Token> actual = new ArrayList<>();
        while (iguanaTokenizer.hasNextToken()) {
            Token token = iguanaTokenizer.nextToken();
            actual.add(token);
        }

        List<Token> expected = Arrays.asList(
            new Token(singleLineComment, "SingleLineComment", input, 0, 21),
            new Token(whitespace, "WhiteSpace", input, 21, 26),
            new Token(var, "Keyword", input, 26, 29),
            new Token(whitespace, "WhiteSpace", input, 29, 30),
            new Token(id, "LetterOrDigits", input, 30, 31),
            new Token(whitespace, "WhiteSpace", input, 31, 32),
            new Token(equals, "=", input, 32, 33),
            new Token(whitespace, "WhiteSpace", input, 33, 34),
            new Token(number, "Number", input, 34, 35)
        );

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
