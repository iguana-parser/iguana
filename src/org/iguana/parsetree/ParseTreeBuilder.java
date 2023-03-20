package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;
import java.util.Set;

public interface ParseTreeBuilder<T> {
    T terminalNode(Terminal terminal, int leftExtent, int rightExtent);

    T nonterminalNode(RuntimeRule rule, List<T> children, int leftExtent, int rightExtent);

    T ambiguityNode(Set<T> node);

    T starNode(Star symbol, List<T> children, int leftExtent, int rightExtent);

    T plusNode(Plus symbol, List<T> children, int leftExtent, int rightExtent);

    T optNode(Opt symbol, T child, int leftExtent, int rightExtent);

    T altNode(Alt symbol, T child, int leftExtent, int rightExtent);

    T groupNode(Group symbol, List<T> children, int leftExtent, int rightExtent);

    T startNode(Start symbol, List<T> children, int leftExtent, int rightExtent);

    T errorNode(int leftExtent, int rightExtent);

    default T metaSymbolNode(Symbol symbol, List<T> children, int leftExtent, int rightExtent) {
        if (symbol instanceof Star) {
            return starNode((Star) symbol, children, leftExtent, rightExtent);
        }
        if (symbol instanceof Plus) {
            return plusNode((Plus) symbol, children, leftExtent, rightExtent);
        }
        if (symbol instanceof Group) {
            return groupNode((Group) symbol, children, leftExtent, rightExtent);
        }
        if (symbol instanceof Alt) {
            return altNode((Alt) symbol, children.get(0), leftExtent, rightExtent);
        }
        if (symbol instanceof Opt) {
            return optNode((Opt) symbol, children.isEmpty() ? null : children.get(0), leftExtent, rightExtent);
        }
        if (symbol instanceof Start) {
            return startNode((Start) symbol, children, leftExtent, rightExtent);
        }
        throw new RuntimeException("Unknown meta symbol type: " + symbol);
    }
}
