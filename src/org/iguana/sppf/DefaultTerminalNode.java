package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;

public class DefaultTerminalNode extends TerminalNode {

    private final int righExtent;
    private final TerminalGrammarSlot slot;

    public DefaultTerminalNode(TerminalGrammarSlot slot, int leftExtent, int righExtent) {
        super(leftExtent);
        this.slot = slot;
        this.righExtent = righExtent;
    }

    @Override
    public int getIndex() {
        return righExtent;
    }

    @Override
    public TerminalGrammarSlot getGrammarSlot() {
        return slot;
    }
}
