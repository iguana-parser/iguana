package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class DefaultTerminalNode extends TerminalNode {

    private final int rightExtent;
    private final TerminalGrammarSlot slot;

    public DefaultTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
        super(leftExtent);
        this.slot = slot;
        this.rightExtent = rightExtent;
    }

    @Override
    public int getRightExtent() {
        return rightExtent;
    }

    @Override
    public TerminalGrammarSlot getGrammarSlot() {
        return slot;
    }
}
