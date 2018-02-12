package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Terminal;

import java.util.Collections;

public class TerminalNode implements ParseTreeNode {

    private final Terminal terminal;
    private final int start;
    private final int end;

    public TerminalNode(Terminal terminal, int start, int end) {
        this.terminal = terminal;
        this.start = start;
        this.end = end;
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
}
