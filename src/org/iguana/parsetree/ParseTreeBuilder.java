package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;
import java.util.Set;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent);
    T nonterminalNode(RuntimeRule rule, List<T> children, int leftExtent, int rightExtent);
    T ambiguityNode(Set<T> node);
    T metaSymbolNode(Symbol symbol, List<T> children, int leftExtent, int rightExtent);
}
