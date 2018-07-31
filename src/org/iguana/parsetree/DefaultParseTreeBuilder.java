package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;
import java.util.Set;

public class DefaultParseTreeBuilder implements ParseTreeBuilder<ParseTreeNode> {

    private Input input;

    public DefaultParseTreeBuilder(Input input) {
        this.input = input;
    }

    @Override
    public TerminalNode terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
        if (leftExtent == rightExtent) return null;
        return new TerminalNode(terminal, leftExtent, rightExtent, input.subString(leftExtent, rightExtent));
    }

    @Override
    public NonterminalNode nonterminalNode(Rule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new NonterminalNode(rule, children, leftExtent, rightExtent);
    }

    @Override
    public AmbiguityNode ambiguityNode(Set<ParseTreeNode> ambiguities) {
        return new AmbiguityNode(ambiguities);
    }

    @Override
    public MetaSymbolNode metaSymbolNode(Symbol symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new MetaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    @Override
    public List<ParseTreeNode> getChildren(ParseTreeNode node) {
       return node.children();
    }

}
