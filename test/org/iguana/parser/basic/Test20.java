package org.iguana.parser.basic;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.SPPFVisualization;
import iguana.parsetrees.tree.Tree;
import iguana.parsetrees.tree.TreeVisualization;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;

/**
 * Created by afroozeh on 28/11/15.
 */
public class Test20 {

    public static void main(String[] args) {
        Nonterminal A = Nonterminal.withName("A");
        Terminal a = Terminal.from(Character.from('a'));
        Rule r1 = Rule.withHead(A).addSymbols(A, a).build();
        Rule r2 = Rule.withHead(A).addSymbol(a).build();
        Grammar grammar = Grammar.builder().addRules(r1, r2).build();

        Input input = Input.fromString("aaa");
        ParseResult result = Iguana.parse(input, grammar, A);
        NonterminalNode sppfNode = result.asParseSuccess().getSPPFNode();
        Tree tree = result.asParseSuccess().getTree();

        SPPFVisualization.generate(result.asParseSuccess().getSPPFNode(), "/Users/afroozeh/output", "sppf");
        TreeVisualization.generate(result.asParseSuccess().getTree(), "/Users/afroozeh/output", "tree");
    }

}
