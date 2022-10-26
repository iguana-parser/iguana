package org.iguana.parser.error;

import org.iguana.ParserTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.ParserTestRunner;
import org.junit.jupiter.api.Test;

public class ErrorRecoveryTest extends ParserTestRunner {

    String grammar =
        "program = stmt+\n" +
        "stmt = expr error ';' | '{' stmt+ error '}' \n" +
        "expr = expr '*' expr > expr '+' expr | [0-9]+\n";

    @Test
    public void test1() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("1/3;")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test2() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("1+2;1/3;1*3;")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test3() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("{1+2}")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test4() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("{1+21/3}")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test5() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("{1/2;}")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test6() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("{1/2;3*4;}")
            .verifyParseTree()
            .build();

        run(test);
    }

    @Test
    public void test7() {
        ParserTest test = ParserTest.newTest()
            .setGrammar(grammar)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("{1/2;3+4}")
            .verifyParseTree()
            .build();

        run(test);
    }
}
