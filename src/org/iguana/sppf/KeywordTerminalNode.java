package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class KeywordTerminalNode extends TerminalNode {

    private final int rightExtent;

    public KeywordTerminalNode(TerminalGrammarSlot slot, int leftExtent) {
        super(slot, leftExtent);
        this.rightExtent = leftExtent + slot.getTerminal().getName().length();
    }

    @Override
    public int getIndex() {
        return rightExtent;
    }
}
