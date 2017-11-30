package org.iguana.sppf;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;

public class NonterminalNodeWithValue extends NonterminalNode {

    private final Object value;

    public NonterminalNodeWithValue(NonterminalGrammarSlot slot, PackedNode child, Object value, Input input) {
        super(slot, child, input);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
