package org.jgll.grammar.slot.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.specialized.FirstTokenSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;


public class NoFirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {
	
	private int headGrammarSlotId;
	private int bodyGrammarSlotId;

	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
			int nontemrinalId,
			List<List<Symbol>> alternates,
			Map<Nonterminal, Set<RegularExpression>> firstSets,
			Map<Nonterminal, Set<RegularExpression>> followSets,
			Map<Nonterminal, List<Set<RegularExpression>>> predictionSets) {
		
		return new HeadGrammarSlot(headGrammarSlotId++, nonterminal, nontemrinalId, alternates, firstSets.get(nonterminal).contains(Epsilon.getInstance()));
	}
	
	@Override
	public TokenGrammarSlot createTokenGrammarSlot(List<Symbol> body,
												   int symbolIndex, 
												   int nodeId,
												   String label,
												   BodyGrammarSlot previous, 
												   int tokenID, 
												   ConditionTest preConditions,
												   ConditionTest postConditions) {
		
		RegularExpression regularExpression = (RegularExpression) body.get(symbolIndex);
		
		if (symbolIndex == 0) {
			return new FirstTokenSlot(bodyGrammarSlotId++, nodeId, label, previous, regularExpression, tokenID, preConditions, postConditions);
		} 
		else {
			return new TokenGrammarSlot(bodyGrammarSlotId++, nodeId, label, previous, regularExpression, tokenID, preConditions, postConditions);
		}
	}

	@Override
	public NonterminalGrammarSlot createNonterminalGrammarSlot(int slotId, String label, BodyGrammarSlot previous,
															   HeadGrammarSlot nonterminal, ConditionTest postConditions) {
		return new NonterminalGrammarSlot(bodyGrammarSlotId++, slotId, label, previous, nonterminal, postConditions);
	}
	
	@Override
	public LastGrammarSlot createLastGrammarSlot(String label, BodyGrammarSlot previous, HeadGrammarSlot head, ConditionTest postConditions) {
		return new LastGrammarSlot(bodyGrammarSlotId++, label, previous, head, postConditions);
	}

	@Override
	public EpsilonGrammarSlot createEpsilonGrammarSlot(String label, HeadGrammarSlot head) {
		return new EpsilonGrammarSlot(bodyGrammarSlotId++, label, head);
	}

}
