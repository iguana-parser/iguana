package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent, Input input);
    T nonterminalNode(Rule rule, List<T> children, int leftExtent, int rightExtent, Input input);
    T visit(AmbiguityNode node);
}
