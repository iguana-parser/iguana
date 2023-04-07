package org.iguana.parser;

import org.iguana.ParserTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.options.ParseOptions;
import org.junit.jupiter.api.Test;

public class NewLineTest extends ParserTestRunner {

    private final ParseOptions parseOptions = new ParseOptions.Builder().build();

    @Test
    public void test1() {
        String grammar = "E = NL";
        ParserTest test = ParserTest.newTest()
                .setGrammar(grammar)
                .setParseOptions(parseOptions)
                .setStartSymbol(Nonterminal.withName("E"))
                .setInput("\n")
                .verifyParseTree()
                .build();
        run(test);
    }

    @Test
    public void test2() {
        String grammar = "E = NL*";
        ParserTest test = ParserTest.newTest()
                .setGrammar(grammar)
                .setParseOptions(parseOptions)
                .setStartSymbol(Nonterminal.withName("E"))
                .setInput("\n\n\n")
                .verifyParseTree()
                .build();
        run(test);
    }
}
