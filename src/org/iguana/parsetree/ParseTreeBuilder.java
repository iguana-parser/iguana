package org.iguana.parsetree;

import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;
import java.util.Set;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent);
    T nonterminalNode(Rule rule, List<T> children, int leftExtent, int rightExtent);
    T ambiguityNode(Set<T> node);
}
