package org.iguana.parsetree;

import org.iguana.grammar.symbol.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public abstract class MetaSymbolNode implements ParseTreeNode {

    protected final Symbol symbol;
    private final int start;
    private final int end;

    public MetaSymbolNode(Symbol symbol, int start, int end) {
        this.symbol = requireNonNull(symbol);
        this.start = start;
        this.end = end;
    }

    @Override
    public String getName() {
        return symbol.getName();
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
    public Symbol getGrammarDefinition() {
        return symbol;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (ParseTreeNode child : children()) {
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
            Objects.equals(children(), that.children());
    }

    @Override
    public int hashCode() {
        return hash(symbol, children(), start, end);
    }


    public abstract static class SingleChildMetaSymbolNode extends MetaSymbolNode {

        private final ParseTreeNode child;

        public SingleChildMetaSymbolNode(Symbol symbol, ParseTreeNode child, int start, int end) {
            super(symbol, start, end);
            this.child = child;
        }

        @Override
        public List<ParseTreeNode> children() {
            if (child == null) return Collections.emptyList();
            else return Collections.singletonList(child);
        }

        /**
         * Returns null when no child is present.
         */
        public ParseTreeNode getChild() {
            return child;
        }
    }

    public abstract static class MultiChildMetaSymbolNode extends MetaSymbolNode {

        private final List<ParseTreeNode> children;

        public MultiChildMetaSymbolNode(Symbol symbol, List<ParseTreeNode> children, int start, int end) {
            super(symbol, start, end);
            this.children = children == null ? Collections.emptyList() : children;
        }

        @Override
        public List<ParseTreeNode> children() {
            return children;
        }
    }

    public static class StarNode extends MultiChildMetaSymbolNode {
        public StarNode(Symbol symbol, List<ParseTreeNode> children, int start, int end) {
            super(symbol, children, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitStarNode(this);
        }
    }

    public static class PlusNode extends MultiChildMetaSymbolNode {
        public PlusNode(Symbol symbol, List<ParseTreeNode> children, int start, int end) {
            super(symbol, children, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitPlusNode(this);
        }
    }

    public static class GroupNode extends MultiChildMetaSymbolNode {
        public GroupNode(Symbol symbol, List<ParseTreeNode> children, int start, int end) {
            super(symbol, children, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitGroupNode(this);
        }
    }

    public static class OptionNode extends SingleChildMetaSymbolNode {
        public OptionNode(Symbol symbol, ParseTreeNode child, int start, int end) {
            super(symbol, child, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitOptionNode(this);
        }
    }

    public static class AltNode extends SingleChildMetaSymbolNode {
        public AltNode(Symbol symbol, ParseTreeNode child, int start, int end) {
            super(symbol, child, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitAltNode(this);
        }
    }

    public static class StartNode extends SingleChildMetaSymbolNode {
        public StartNode(Symbol symbol, ParseTreeNode child, int start, int end) {
            super(symbol, child, start, end);
        }

        @Override
        public <T> Object accept(ParseTreeVisitor<T> visitor) {
            return visitor.visitStartNode(this);
        }
    }
}
