package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class EmptyTerminalNode extends TerminalNode {

    private final TerminalGrammarSlot slot;

    public EmptyTerminalNode(TerminalGrammarSlot slot, int leftExtent) {
        super(leftExtent);
        this.slot = slot;
    }

    @Override
    public int getIndex() {
        return getLeftExtent();
    }

    @Override
    public TerminalGrammarSlot getGrammarSlot() {
        return slot;
    }
}
