package org.iguana.sppf;

import org.iguana.grammar.slot.NonterminalGrammarSlot;

public class NonterminalNodeWithValue extends NonterminalNode {

    private final Object value;

    public NonterminalNodeWithValue(NonterminalGrammarSlot slot, int rightExtent, Object value) {
        super(slot, rightExtent);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
