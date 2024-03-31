package org.iguana.sppf;

import org.iguana.grammar.symbol.Terminal;

public class EpsilonTerminalNode extends TerminalNode {

    public EpsilonTerminalNode(int leftExtent) {
        super(Terminal.epsilon(), leftExtent);
    }

    @Override
    public int getRightExtent() {
        return getLeftExtent();
    }
}
