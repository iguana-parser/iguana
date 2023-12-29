package org.iguana.grammar.slot;

public class GrammarSlotUtil {
    public static EndGrammarSlot getEndSlot(BodyGrammarSlot slot) {
        BodyGrammarSlot curr = slot;
        BodyGrammarSlot end = slot;
        while (curr != null) {
            end = curr;
            if (curr.getOutTransition() == null) {
                curr = null;
            } else {
                curr = curr.getOutTransition().destination();
            }
        }
        return (EndGrammarSlot) end;
    }
}
