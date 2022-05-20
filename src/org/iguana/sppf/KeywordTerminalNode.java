package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class KeywordTerminalNode extends TerminalNode {

    private final int rightExtent;
    private final TerminalGrammarSlot slot;

    public KeywordTerminalNode(TerminalGrammarSlot slot, int leftExtent) {
        super(leftExtent);
        this.slot = slot;
        this.rightExtent = leftExtent + slot.getTerminal().getName().length();
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
