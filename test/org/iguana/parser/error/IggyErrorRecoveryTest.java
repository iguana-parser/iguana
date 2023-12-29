package org.iguana.parser.error;

import org.iguana.ParserTest;
import org.iguana.grammar.symbol.Start;
import org.iguana.iggy.gen.IggyGrammar;
import org.iguana.parser.ParserTestRunner;
import org.iguana.parser.options.ParseOptions;
import org.junit.jupiter.api.Test;

public class IggyErrorRecoveryTest extends ParserTestRunner {

    private final ParseOptions parseOptions = new ParseOptions.Builder().setErrorRecoveryEnabled(true).build();

    @Test
    public void test1() {
        Start start = IggyGrammar.getGrammar().getStartSymbols().get(0);
        String input = "A = 1 B;";
        ParserTest test = ParserTest.newTest()
                                    .setParseOptions(parseOptions)
                                    .setStartSymbol(start)
                                    .setInput(input)
                                    .verifyParseTree()
                                    .build();

        run(test, IggyGrammar.getGrammar());
    }
}
