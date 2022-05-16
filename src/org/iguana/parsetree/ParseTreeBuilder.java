package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;

import java.util.LinkedHashSet;
import java.util.List;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent);
    T nonterminalNode(RuntimeRule rule, List<T> children, int leftExtent, int rightExtent);
    T ambiguityNode(LinkedHashSet<T> node);
    T metaSymbolNode(Symbol symbol, List<T> children, int leftExtent, int rightExtent);
    List<T> getChildren(T node);
}
