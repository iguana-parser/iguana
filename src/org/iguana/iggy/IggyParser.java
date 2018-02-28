package org.iguana.iggy;

import iguana.utils.input.Input;
import iguana.utils.visualization.GraphVizUtil;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.JsonSerializer;
import org.iguana.util.visualization.ParseTreeToDot;
import org.iguana.util.visualization.SPPFToDot;

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

        Grammar iggyGrammar = iggyGrammar();
        Start start = iggyGrammar.getStartSymbol(Nonterminal.withName("Definition"));
        Input input = Input.fromPath("/Users/Ali/workspace-thesis/iguana/test/grammars/basic/Test1/grammar.iggy");
        ParseResult result = Iguana.parse(input, iggyGrammar, start);
        if (result.isParseSuccess()) {
            SPPFToDot toDot = new SPPFToDot(input);
            toDot.visit(result.asParseSuccess().getSPPFNode());
            GraphVizUtil.generateGraph(toDot.getString(), "/Users/Ali/workspace-thesis/sppf.pdf");
            Grammar grammar = (Grammar) result.asParseSuccess().getParseTree(new ParseTreeBuilder<Object>() {


                @Override
                public Object terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
                    return null;
                }

                @Override
                public Object nonterminalNode(Rule rule, List<Object> children, int leftExtent, int rightExtent) {
                    if (rule.isLayout()) return null;

                    switch (rule.getHead().getName()) {
                        case "Start_Definition":
                            return children.get(0);

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

                @Override
                public Object metaSymbolNode(Symbol symbol, List<Object> children, int leftExtent, int rightExtent) {
                    return null;
                }
            });
            System.out.println(grammar);
        }
    }

}
