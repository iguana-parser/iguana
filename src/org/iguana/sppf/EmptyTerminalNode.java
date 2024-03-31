package org.iguana.sppf;

import org.iguana.grammar.symbol.Terminal;

public class EmptyTerminalNode extends TerminalNode {

    public EmptyTerminalNode(Terminal terminal, int leftExtent) {
        super(terminal, leftExtent);
    }

    @Override
    public int getRightExtent() {
        return getLeftExtent();
    }
}
