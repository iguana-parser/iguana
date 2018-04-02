package org.iguana.parser.descriptor;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNode;

public interface ResultOps<T> {
    T dummy();
    T base(TerminalGrammarSlot slot, int start, int end);
    T merge(T current, T result1, T result2, BodyGrammarSlot slot);
    T convert(T current, T result, EndGrammarSlot slot, Object value);
    int getRightIndex(T result);
    int getRightIndex(T result, GSSNode<T> gssNode);
    Object getValue(T result);
}
