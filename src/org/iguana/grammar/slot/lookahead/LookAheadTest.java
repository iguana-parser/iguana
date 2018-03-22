package org.iguana.grammar.slot.lookahead;

import org.iguana.grammar.slot.BodyGrammarSlot;

import java.util.List;

@FunctionalInterface
public interface LookAheadTest<T> {
	
	/*
	 * Returns a list of first slots that can be parsed
	 * at the given input character. 
	 */
	List<BodyGrammarSlot<T>> get(int v);
	
}
