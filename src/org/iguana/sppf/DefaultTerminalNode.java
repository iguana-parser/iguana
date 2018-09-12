package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class DefaultTerminalNode extends TerminalNode {

    private final int righExtent;

    public DefaultTerminalNode(TerminalGrammarSlot slot, int leftExtent, int righExtent) {
        super(slot, leftExtent);
        this.righExtent = righExtent;
    }

    @Override
    public int getIndex() {
        return righExtent;
    }
}
