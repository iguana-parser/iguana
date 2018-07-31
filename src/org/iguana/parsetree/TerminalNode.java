package org.iguana.parsetree;

import org.iguana.grammar.symbol.Terminal;

import static iguana.utils.Assert.requireNonNegative;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class TerminalNode implements ParseTreeNode {

    private final Terminal terminal;
    private final int start;
    private final int end;
    private final String text;

    public TerminalNode(Terminal terminal, int start, int end, String text) {
        this.terminal = requireNonNull(terminal);
        this.start = requireNonNegative(start);
        this.end = requireNonNegative(end);
        this.text = requireNonNull(text);
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
    public String getName() {
        return terminal.getName();
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visitTerminalNode(this);
    }

    @Override
    public Terminal getGrammarDefinition() {
        return terminal;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TerminalNode)) return false;
        TerminalNode other = (TerminalNode) obj;
        return terminal.equals(other.terminal) && start == other.start && end == other.end;
    }

    @Override
    public int hashCode() {
        return hash(terminal, start, end);
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %d)", terminal.getName(), start, end);
    }
}
