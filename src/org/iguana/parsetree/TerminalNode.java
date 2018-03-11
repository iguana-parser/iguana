package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Terminal;

import java.util.Collections;
import static java.util.Objects.*;
import static iguana.utils.Assert.*;

public class TerminalNode implements ParseTreeNode {

    private final Terminal terminal;
    private final int start;
    private final int end;

    public TerminalNode(Terminal terminal, int start, int end) {
        this.terminal = requireNonNull(terminal);
        this.start = requireNonNegative(start);
        this.end = requireNonNegative(end);
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
        return Collections.emptyList();
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Terminal definition() {
        return terminal;
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
