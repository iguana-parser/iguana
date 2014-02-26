package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Nonterminal;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal);
	
}
