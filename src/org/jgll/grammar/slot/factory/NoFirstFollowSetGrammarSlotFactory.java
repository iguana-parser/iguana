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
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;


public class NoFirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {

	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
			Set<List<Symbol>> alternates,
			Map<Nonterminal, Set<RegularExpression>> firstSets,
			Map<Nonterminal, Set<RegularExpression>> followSets,
			Map<Nonterminal, List<Set<RegularExpression>>> predictionSets) {
		
		return new HeadGrammarSlot(nonterminal, alternates, firstSets.get(nonterminal).contains(Epsilon.getInstance()));
	}
	
	@Override
	public TokenGrammarSlot createTokenGrammarSlot(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, HeadGrammarSlot head, int tokenID) {
		if(regularExpression instanceof Character) {
			return new CharacterGrammarSlot(rule, position, slotId, label, previous, (Character) regularExpression, head, tokenID);
		}
		else if (regularExpression instanceof Range) {
			Range r = (Range) regularExpression;
			if(r.getStart() == r.getEnd()) {
				return new CharacterGrammarSlot(rule, position, slotId, label, previous, new Character(r.getStart()), head, tokenID);
			} else {
				return new RangeGrammarSlot(rule, position, slotId, label, previous, r, head, tokenID);
			}
		}
		return new TokenGrammarSlot(rule, position, slotId, label, previous, regularExpression, head, tokenID);
	}

	@Override
	public NonterminalGrammarSlot createNonterminalGrammarSlot(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		return new NonterminalGrammarSlot(rule, position, slotId, label, previous, nonterminal, head);
	}
	
	@Override
	public LastGrammarSlot createLastGrammarSlot(Rule rule, int position, int slotId, String label,
			BodyGrammarSlot previous, HeadGrammarSlot head, Serializable object) {
		return new LastGrammarSlot(rule, position, slotId, label, previous, head, object);
	}

	@Override
	public EpsilonGrammarSlot createEpsilonGrammarSlot(Rule rule, int position, int slotId, String label, HeadGrammarSlot head, Serializable object) {
		return new EpsilonGrammarSlot(rule, position, slotId, label, head, object);
	}

}
