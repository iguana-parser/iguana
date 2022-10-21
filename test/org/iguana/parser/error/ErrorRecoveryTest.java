package org.iguana.parser.error;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.parser.IguanaParser;
import org.iguana.util.visualization.ParseTreeToDot;
import org.iguana.util.visualization.SPPFToDot;
import org.iguana.utils.input.Input;
import org.iguana.utils.visualization.DotGraph;
import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;

public class ErrorRecoveryTest {

    @Test
    public void test() throws Exception {
        String grammarText =
            "program = stmt\n" +
            "stmt = expr error ';'\n" +
            "expr = expr '*' expr > expr '+' expr | [0-9]+\n";

        Grammar grammar = fromIggyGrammar(grammarText);
        IguanaParser parser = new IguanaParser(grammar);
        Input input = Input.fromString("1/3;");
        parser.parse(input, Nonterminal.withName("program"));
        DotGraph parseTreeDotGraph = ParseTreeToDot.getDotGraph(parser.getParseTree(), input);
        parseTreeDotGraph.generate("/Users/afroozeh/error.pdf");
    }
}
