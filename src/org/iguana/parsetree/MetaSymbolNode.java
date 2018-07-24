package org.iguana.parsetree;

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

    @Override
    public String getName() {
        return symbol.getName();
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
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public List<ParseTreeNode> children() {
        return children;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visitMetaSymbolNode(this);
    }

    @Override
    public Symbol getGrammarDefinition() {
        return symbol;
    }
}
