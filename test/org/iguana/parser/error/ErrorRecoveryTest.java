package org.iguana.parser.error;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.parser.IguanaParser;
import org.iguana.utils.input.Input;
import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;

public class ErrorRecoveryTest {

    @Test
    public void test() {
        String grammarText =
            "program = stmt+\n" +
            "stmt = expr error ';'\n" +
            "expr = expr '*' expr > expr '+' expr | [0-9]+\n";

        Grammar grammar = fromIggyGrammar(grammarText);
        IguanaParser parser = new IguanaParser(grammar);
        parser.parse(Input.fromString("1+2;1+3;1*4+2;"), Nonterminal.withName("program"));
    }
}
