package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.utils.input.Input;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class IguanaTokenizerTest {

    @Test
    public void test() throws IOException {
        RuntimeGrammar grammar = IggyParser.iggyGrammar().toRuntimeGrammar();
        System.out.println(grammar.getLiterals());
        System.out.println(grammar.getRegularExpressions());

        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(grammar.getRegularExpressions(), grammar.getLiterals(), Input.fromFile(new File(path)), 0);
        while (iguanaTokenizer.hasNextToken()) {
            System.out.print(iguanaTokenizer.nextToken().getLexeme());
        }
    }
}
