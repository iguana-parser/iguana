package org.iguana.parsetree;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;
import java.util.Set;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent);
    T nonterminalNode(Rule rule, List<T> children, int leftExtent, int rightExtent);
    T ambiguityNode(Set<T> node);
    T metaSymbolNode(Symbol symbol, List<T> children, int leftExtent, int rightExtent);

    default T getByIndex(Rule rule, List<T> children, int index) {
        if (index < 0 || index >= children.size()) throw new IndexOutOfBoundsException();
        return children.get(index);
    }

    default T getByName(Rule rule, List<T> children, String name) {
        int i = 0;
        for (Symbol s : rule.getBody()) {
            if (s.getName().equals(name))
                return children.get(i);
            i++;
        }
        return null;
    }
}
