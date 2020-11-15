package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Terminal;

public class DefaultTerminalNode extends TerminalNode {

    private final Input input;

    public DefaultTerminalNode(Terminal terminal, int start, int end, Input input) {
        super(terminal, start, end);
        this.input = input;
    }

    @Override
    public String getText() {
        return input.subString(getStart(), getEnd());
    }
}
