package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;
import org.iguana.utils.input.Input;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class IguanaTokenizerTest {

    @Test
    public void test() throws IOException {
        RuntimeGrammar grammar = IggyParser.iggyGrammar().toRuntimeGrammar();
        System.out.println(grammar.getLiterals());
        System.out.println(grammar.getRegularExpressions());

        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        Map<String, RegularExpression> regularExpressions = new HashMap<>();
        regularExpressions.put("Identifier", grammar.getRegularExpressions().get("LetterOrDigits"));
        regularExpressions.put("Number", grammar.getRegularExpressions().get("Number"));
        regularExpressions.put("Char", grammar.getRegularExpressions().get("Char"));
        regularExpressions.put("String", grammar.getRegularExpressions().get("String"));
        regularExpressions.put("WhiteSpace", grammar.getRegularExpressions().get("WhiteSpace"));
        regularExpressions.put("SingleLineComment", grammar.getRegularExpressions().get("SingleLineComment"));
        regularExpressions.put("MultiLineComment", grammar.getRegularExpressions().get("MultiLineComment"));

        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(grammar.getRegularExpressions(), Input.fromFile(new File(path)), 0);
        while (iguanaTokenizer.hasNextToken()) {
            System.out.print(iguanaTokenizer.nextToken().getLexeme());
        }
    }
}
