package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Symbol;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class MetaSymbolNode implements ParseTreeNode {

    private final Symbol symbol;
    private final List<ParseTreeNode> children;
    private final int start;
    private final int end;

    public MetaSymbolNode(Symbol symbol, List<ParseTreeNode> children, int start, int end) {
        this.symbol = requireNonNull(symbol);
        this.children = children != null ? children : Collections.emptyList();
        this.start = start;
        this.end = end;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MetaSymbolNode)) return false;
        MetaSymbolNode other = (MetaSymbolNode) obj;
        return symbol.equals(other.symbol) && children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return hash(symbol, children);
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
        return children;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object definition() {
        return symbol;
    }
}
