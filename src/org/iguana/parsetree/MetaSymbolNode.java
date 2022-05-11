package org.iguana.parsetree;

import org.iguana.grammar.symbol.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public Object accept(ParseTreeVisitor visitor) {
        return visitor.visitMetaSymbolNode(this);
    }

    @Override
    public Symbol getGrammarDefinition() {
        return symbol;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (ParseTreeNode child : children) {
            sb.append(child.getText());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %d)", symbol, start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaSymbolNode)) return false;
        MetaSymbolNode that = (MetaSymbolNode) o;
        return Objects.equals(symbol, that.symbol) &&
            start == that.start &&
            end == that.end &&
            Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return hash(symbol, children, start, end);
    }
}
