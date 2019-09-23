package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.junit.Test;

import java.nio.file.Paths;

public class IggyParserTest {

    @Test
    public void test() throws Exception {
        String path = Paths.get("test/resources/Grammar.iggy").toAbsolutePath().toString();
        Grammar grammar = IggyParser.getGrammar(path);
        System.out.println(grammar);
    }
}
