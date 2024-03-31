package org.iguana.result;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;

public interface ResultOps<T extends Result> {

    T dummy();

    T base(Terminal terminal, int start, int end);

    T error(BodyGrammarSlot slot, int start, int end);

    T error(BodyGrammarSlot slot, int start, int end, List<T> children);

    T merge(T current, T result1, T result2, BodyGrammarSlot slot);

    T convert(T current, T result, EndGrammarSlot slot, Object value);
}
