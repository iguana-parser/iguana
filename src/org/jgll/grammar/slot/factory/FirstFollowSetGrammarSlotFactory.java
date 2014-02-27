package org.jgll.grammar.slot.factory;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlotWithFirstFollowCheck;
import org.jgll.grammar.symbol.Nonterminal;


public class FirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {

	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal) {
		return new HeadGrammarSlotWithFirstFollowCheck(nonterminal);
	}

}
