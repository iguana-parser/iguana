package org.jgll.grammar.slot.factory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.CharacterGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.RangeGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;


public class NoFirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {

	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
			int nontemrinalId,
			Set<List<Symbol>> alternates,
			Map<Nonterminal, Set<RegularExpression>> firstSets,
			Map<Nonterminal, Set<RegularExpression>> followSets,
			Map<Nonterminal, List<Set<RegularExpression>>> predictionSets) {
		
		return new HeadGrammarSlot(nonterminal, nontemrinalId, alternates, firstSets.get(nonterminal).contains(Epsilon.getInstance()));
	}
	
	@Override
	public TokenGrammarSlot createTokenGrammarSlot(int slotId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, HeadGrammarSlot head, int tokenID) {
		if(regularExpression instanceof Character) {
			return new CharacterGrammarSlot(slotId, label, previous, (Character) regularExpression, head, tokenID);
		}
		else if (regularExpression instanceof Range) {
			Range r = (Range) regularExpression;
			if(r.getStart() == r.getEnd()) {
				return new CharacterGrammarSlot(slotId, label, previous, new Character(r.getStart()), head, tokenID);
			} else {
				return new RangeGrammarSlot(slotId, label, previous, r, head, tokenID);
			}
		}
		return new TokenGrammarSlot(slotId, label, previous, regularExpression, head, tokenID);
	}

	@Override
	public NonterminalGrammarSlot createNonterminalGrammarSlot(int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		return new NonterminalGrammarSlot(slotId, label, previous, nonterminal, head);
	}
	
	@Override
	public LastGrammarSlot createLastGrammarSlot(int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		return new LastGrammarSlot(slotId, label, previous, head);
	}

	@Override
	public EpsilonGrammarSlot createEpsilonGrammarSlot(int slotId, String label, HeadGrammarSlot head) {
		return new EpsilonGrammarSlot(slotId, label, head);
	}

}
