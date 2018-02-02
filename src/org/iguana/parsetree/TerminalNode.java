package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Terminal;

import java.util.Collections;

public class TerminalNode implements ParseTreeNode {

    private final Input input;
    private final int start;
    private final int end;
    private final Terminal terminal;

    public TerminalNode(Terminal terminal, int start, int end, Input input) {
        this.terminal = terminal;
        this.start = start;
        this.end = end;
        this.input = input;
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
    public String text() {
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
