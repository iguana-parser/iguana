package org.iguana.parser.error;

import org.iguana.ParserTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.ParserTestRunner;
import org.junit.jupiter.api.Test;

public class ErrorRecoveryTest extends ParserTestRunner {

    @Test
    public void test1() throws Exception {
        String grammarText =
            "program = stmt+\n" +
                "stmt = expr error ';'\n" +
                "expr = expr '*' expr > expr '+' expr | [0-9]+\n";

        ParserTest test = ParserTest.newTest()
            .setGrammar(grammarText)
            .setStartSymbol(Nonterminal.withName("program"))
            .setInput("1/3;")
            .verifyParseTree()
            .build();

        run(test);
    }
}
