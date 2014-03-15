package org.jgll.grammar.slot.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.CharacterGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlotArrayFirstFollow;
import org.jgll.grammar.slot.HeadGrammarSlotTreeMapFirstFollow;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlotFirstFollow;
import org.jgll.grammar.slot.RangeGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

public class FirstFollowSetGrammarSlotFactory implements GrammarSlotFactory {
	
	private int headGrammarSlotId;
	private int bodyGrammarSlotId;
	
	@Override
	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 int nonterminalId,
												 List<List<Symbol>> alternates,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets,
												 Map<Nonterminal, List<Set<RegularExpression>>> predictionSets) {
		
		Set<RegularExpression> set = new HashSet<>(firstSets.get(nonterminal));
		if(set.contains(Epsilon.getInstance())) {
			set.addAll(followSets.get(nonterminal));
		}
		set.remove(Epsilon.getInstance());
		
		List<Range> ranges = new ArrayList<>();
		for(RegularExpression regex : set) {
			for(Range range : regex.getFirstSet()) {
				ranges.add(range);
			}
		}
		
		Collections.sort(ranges);
		
		assert ranges.size() > 0;
		
		int min = ranges.get(0).getStart();
		int max = ranges.get(ranges.size() - 1).getEnd();
		
		boolean nullable = firstSets.get(nonterminal).contains(Epsilon.getInstance());
		List<Set<RegularExpression>> predictionSet = predictionSets.get(nonterminal);
		
		if(max - min < 10000) {
			return new HeadGrammarSlotArrayFirstFollow(headGrammarSlotId++, nonterminal, nonterminalId, alternates, predictionSet, nullable, min, max);
		}
		
		return new HeadGrammarSlotTreeMapFirstFollow(headGrammarSlotId++, nonterminal, nonterminalId, alternates, predictionSet, nullable);
	}
	

	@Override
	public NonterminalGrammarSlot createNonterminalGrammarSlot(int nodeId,
															   String label,
															   BodyGrammarSlot previous, 
															   HeadGrammarSlot nonterminal, 
															   HeadGrammarSlot head) {
		return new NonterminalGrammarSlotFirstFollow(bodyGrammarSlotId++, nodeId, label, previous, nonterminal, head);
	}

	@Override
	public TokenGrammarSlot createTokenGrammarSlot(int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, 
												   HeadGrammarSlot head, int tokenID) {
		if(regularExpression instanceof Character) {
			return new CharacterGrammarSlot(bodyGrammarSlotId++, nodeId, label, previous, (Character) regularExpression, head, tokenID);
		}
		else if (regularExpression instanceof Range) {
			Range r = (Range) regularExpression;
			if(r.getStart() == r.getEnd()) {
				return new CharacterGrammarSlot(bodyGrammarSlotId++, nodeId, label, previous, new Character(r.getStart()), head, tokenID);
			} else {
				return new RangeGrammarSlot(bodyGrammarSlotId++, nodeId, label, previous, r, head, tokenID);
			}
		}
		return new TokenGrammarSlot(bodyGrammarSlotId++, nodeId, label, previous, regularExpression, head, tokenID);
	}


	@Override
	public LastGrammarSlot createLastGrammarSlot(int slotId, String label,
			BodyGrammarSlot previous, HeadGrammarSlot head) {
		return new LastGrammarSlot(slotId, label, previous, head);
	}
	
	@Override
	public EpsilonGrammarSlot createEpsilonGrammarSlot(int slotId, String label, HeadGrammarSlot head) {
		return new EpsilonGrammarSlot(slotId, label, head);
	}
	
}
