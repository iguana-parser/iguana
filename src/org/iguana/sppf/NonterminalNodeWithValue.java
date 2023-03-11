package org.iguana.sppf;

import org.iguana.grammar.slot.EndGrammarSlot;

public class NonterminalNodeWithValue extends NonterminalNode {

    private final Object value;

    public NonterminalNodeWithValue(
            EndGrammarSlot slot, NonPackedNode child, int leftExtent, int rightExtent, Object value) {
        super(slot, child, leftExtent, rightExtent);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
