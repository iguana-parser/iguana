package org.iguana.sppf;

import org.iguana.grammar.symbol.Terminal;

public class DefaultTerminalNode extends TerminalNode {

    private final int rightExtent;

    public DefaultTerminalNode(Terminal terminal, int leftExtent, int rightExtent) {
        super(terminal, leftExtent);
        this.rightExtent = rightExtent;
    }

    @Override
    public int getRightExtent() {
        return rightExtent;
    }
}
