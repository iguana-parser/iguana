package org.iguana.parsetree;

import org.iguana.grammar.symbol.Terminal;

import static org.iguana.utils.Assert.requireNonNegative;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public abstract class TerminalNode implements ParseTreeNode {

    private final Terminal terminal;
    private final int start;
    private final int end;

    public TerminalNode(Terminal terminal, int start, int end) {
        this.terminal = requireNonNull(terminal);
        this.start = requireNonNegative(start);
        this.end = requireNonNegative(end);
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
    public Object accept(ParseTreeVisitor visitor) {
        return visitor.visitTerminalNode(this);
    }

    @Override
    public Terminal getGrammarDefinition() {
        return terminal;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public abstract String getText();

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
