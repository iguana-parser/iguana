package org.iguana.parser;

import org.iguana.ParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.parser.options.ParseOptions;
import org.iguana.util.visualization.GrammarGraphToDot;
import org.iguana.utils.visualization.DotGraph;
import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;

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
        String grammar = "E = NL";
        ParserTest test = ParserTest.newTest()
                .setGrammar(grammar)
                .setParseOptions(parseOptions)
                .setStartSymbol(Nonterminal.withName("E"))
                .setInput("\n\n\n")
                .verifyParseTree()
                .build();
        run(test);
    }

//    @Test
    public void test3() throws Exception {
        String grammarText = "E = S = A B L '\n'\n"
                         + "A = 'a'\n"
                         + "B = 'b' | \n"
                         + "regex L = [\\ \\n]*";
        Grammar grammar = fromIggyGrammar(grammarText);
        RuntimeGrammar runtimeGrammar = GrammarTransformer.transform(grammar.toRuntimeGrammar());
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(runtimeGrammar);
        DotGraph dotGraph = GrammarGraphToDot.toDot(grammarGraph, DotGraph.Direction.LEFT_TO_RIGHT);
        dotGraph.generate("/Users/afroozeh/grammarGraph.pdf");

//        ParserTest test = ParserTest.newTest()
//                                    .setGrammar(grammar)
//                                    .setParseOptions(parseOptions)
//                                    .setStartSymbol(Nonterminal.withName("E"))
//                                    .setInput("\n\n\n")
//                                    .verifyParseTree()
//                                    .build();
//        run(test);
    }
}
