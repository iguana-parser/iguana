package org.iguana.parsetree;

import org.iguana.grammar.symbol.*;

import java.util.List;
import java.util.Set;

public class DefaultParseTreeBuilder implements ParseTreeBuilder<ParseTreeNode> {

    @Override
    public ParseTreeNode terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
        if (leftExtent == rightExtent) return null;
        return new TerminalNode(terminal, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode nonterminalNode(Rule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new NonterminalNode(rule, children, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode ambiguityNode(Set<ParseTreeNode> ambiguities) {
        return new AmbiguityNode(ambiguities);
    }

    @Override
    public ParseTreeNode metaSymbolNode(Symbol symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new MetaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

}
