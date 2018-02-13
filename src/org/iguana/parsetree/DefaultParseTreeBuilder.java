package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;

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
}
