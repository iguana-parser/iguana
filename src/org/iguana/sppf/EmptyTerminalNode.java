package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class EmptyTerminalNode extends TerminalNode {

    public EmptyTerminalNode(TerminalGrammarSlot slot, int leftExtent) {
        super(slot, leftExtent);
    }

    @Override
    public int getIndex() {
        return getLeftExtent();
    }
}
