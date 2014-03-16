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
import org.jgll.grammar.slot.LastGrammarSlotFirstFollow;
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
		
		Set<RegularExpression> firstSet = firstSets.get(nonterminal);
		Set<RegularExpression> followSet = followSets.get(nonterminal);
		
		Set<RegularExpression> set = new HashSet<>(firstSet);
		if(set.contains(Epsilon.getInstance())) {
			set.addAll(followSet);
		}
		set.remove(Epsilon.getInstance());
		
		List<Range> rangesPredictionSet = getSortedRanges(set);
		int minPredictionSet = getMin(rangesPredictionSet);
		int maxPredictionSet = getMax(rangesPredictionSet);
		
		boolean nullable = firstSet.contains(Epsilon.getInstance());
		List<Set<RegularExpression>> predictionSet = predictionSets.get(nonterminal);
		
		List<Range> rangesFollowSet = getSortedRanges(followSet);
		int minFollowSet = getMin(rangesFollowSet);
		int maxFollowSet = getMax(rangesFollowSet);
		
		if(maxPredictionSet - minPredictionSet < 10000) {
			return new HeadGrammarSlotArrayFirstFollow(headGrammarSlotId++, 
													   nonterminal, 
													   nonterminalId, 
													   alternates, 
													   followSet, 
													   predictionSet, nullable, 
													   minPredictionSet, maxPredictionSet,
													   minFollowSet, maxFollowSet);
		}
		
		return new HeadGrammarSlotTreeMapFirstFollow(headGrammarSlotId++, nonterminal, nonterminalId, alternates, predictionSet, nullable);
	}
	
	private int getMin(List<Range> ranges) {
		return ranges.get(0).getStart();
	}
	
	private int getMax(List<Range> ranges) {
		return ranges.get(ranges.size() - 1).getEnd();
	}
	
	private List<Range> getSortedRanges(Set<RegularExpression> set) {
		List<Range> ranges = new ArrayList<>();
		for(RegularExpression regex : set) {
			for(Range range : regex.getFirstSet()) {
				ranges.add(range);
			}
		}
		
		Collections.sort(ranges);
		
		assert ranges.size() > 0;
		
		return ranges;
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
	public LastGrammarSlot createLastGrammarSlot(String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		return new LastGrammarSlotFirstFollow(bodyGrammarSlotId++, label, previous, head);
	}
	
	@Override
	public EpsilonGrammarSlot createEpsilonGrammarSlot(String label, HeadGrammarSlot head) {
		return new EpsilonGrammarSlot(bodyGrammarSlotId++, label, head);
	}
	
}
