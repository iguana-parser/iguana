package org.iguana.grammar.slot.lookahead;

import java.util.List;

import org.iguana.grammar.slot.BodyGrammarSlot;

@FunctionalInterface
public interface LookAheadTest {
	
	/**
	 * Returns a list of first slots that can be parsed
	 * at the given input character. 
	 */
	List<BodyGrammarSlot> get(int v);
	
}
