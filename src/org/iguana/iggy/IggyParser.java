package org.iguana.iggy;

import iguana.utils.input.Input;
import iguana.utils.visualization.GraphVizUtil;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.JsonSerializer;
import org.iguana.util.visualization.ParseTreeToDot;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static iguana.utils.io.FileUtils.readFile;

public class IggyParser {

    private static Grammar iggyGrammar() {
        try {
            String content = readFile(IggyParser.class.getResourceAsStream("/iggy.json"));
            return JsonSerializer.deserialize(content, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
//        Grammar g = Grammar.load(new File("/Users/afroozeh/iggy"));
//        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
//        precedenceAndAssociativity.setOP2();
//
//        g = new EBNFToBNF().transform(g);
//        g = precedenceAndAssociativity.transform(g);
//        g = new LayoutWeaver().transform(g);
//        g.getStartSymbol(Nonterminal.withName("Definition"));
//        System.out.println(JsonSerializer.toJSON(g));

        Grammar grammar = iggyGrammar();
        Start start = grammar.getStartSymbol(Nonterminal.withName("Definition"));
        Input input = Input.fromPath("/Users/afroozeh/workspace/iguana/test/grammars/basic/Test1/grammar.iggy");
        ParseResult result = Iguana.parse(input, grammar, start);
        if (result.isParseSuccess()) {
            ParseTreeNode parseTree = (ParseTreeNode) result.asParseSuccess().getParseTree(new ParseTreeBuilder<Object>() {
                @Override
                public Object terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
                    return null;
                }

                @Override
                public Object nonterminalNode(Rule rule, List<Object> children, int leftExtent, int rightExtent) {
                    if (rule.isLayout()) return null;

                    switch (rule.getHead().getName()) {
                        case "Definition":
                            Grammar.Builder builder = Grammar.builder();
                            for (Object child : children) {
                                if (child != null) {
                                    builder.addRule((Rule) child);
                                }
                            }
                            return builder.build();
                    }
                    throw new RuntimeException("Should not reach here");
                }

                @Override
                public Object ambiguityNode(Set<Object> node) {
                    return null;
                }
            });
            String dot = new ParseTreeToDot().toDot(parseTree, input);
            GraphVizUtil.generateGraph(dot, "/Users/afroozeh/output.pdf");
        }
    }

}
