package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Symbol;

import java.util.List;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class MetaSymbolNode implements ParseTreeNode {

    private final Symbol star;
    private final List<ParseTreeNode> symbols;
    private final int start;
    private final int end;

    public MetaSymbolNode(Symbol symbol, List<ParseTreeNode> symbols, int start, int end) {
        this.star = requireNonNull(symbol);
        this.symbols = symbols;
        this.start = start;
        this.end = end;
    }

    public Symbol getStar() {
        return star;
    }

    public List<ParseTreeNode> getSymbols() {
        return symbols;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MetaSymbolNode)) return false;
        MetaSymbolNode other = (MetaSymbolNode) obj;
        return star.equals(other.star) && symbols.equals(other.symbols);
    }

    @Override
    public int hashCode() {
        return hash(star, symbols);
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public String text(Input input) {
        return input.subString(start, end);
    }

    @Override
    public Iterable<ParseTreeNode> children() {
        return symbols;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object definition() {
        return star;
    }
}
