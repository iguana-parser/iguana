package org.iguana.sppf;

import org.iguana.grammar.symbol.Terminal;

public class KeywordTerminalNode extends TerminalNode {

    private final int rightExtent;

    public KeywordTerminalNode(Terminal terminal, int leftExtent) {
        super(terminal, leftExtent);
        this.rightExtent = leftExtent + terminal.getRegularExpression().length();
    }

    @Override
    public int getRightExtent() {
        return rightExtent;
    }

}
