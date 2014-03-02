package org.jgll.grammar.slot.factory;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.CharacterGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.RangeGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;


public class NoFirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {

	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal) {
		return new HeadGrammarSlot(nonterminal);
	}
	
	@Override
	public TokenGrammarSlot createTokenGrammarSlot(int position, BodyGrammarSlot previous, RegularExpression regularExpression, HeadGrammarSlot head, int tokenID) {
		if(regularExpression instanceof Character) {
			return new CharacterGrammarSlot(position, previous, (Character) regularExpression, head, tokenID);
		}
		else if (regularExpression instanceof Range) {
			Range r = (Range) regularExpression;
			if(r.getStart() == r.getEnd()) {
				return new CharacterGrammarSlot(position, previous, new Character(r.getStart()), head, tokenID);
			} else {
				return new RangeGrammarSlot(position, previous, r, head, tokenID);
			}
		}
		return new TokenGrammarSlot(position, previous, regularExpression, head, tokenID);
	}

}
