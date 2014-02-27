package org.jgll.grammar.slot.factory;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal);
	
}
