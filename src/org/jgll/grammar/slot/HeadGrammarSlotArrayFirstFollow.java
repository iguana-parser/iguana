package org.jgll.grammar.slot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class HeadGrammarSlotArrayFirstFollow extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A mapping from an input value to a set of alternatives of this nonterminal
	 * that can be predicted for that input value.
	 */
	private Set<Integer>[] predictionMap;
	
	private boolean[] followSetMap;
	
	private final int minPredictionSet;
	
	private final int maxPredictionSet;
	
	private final int minFollowSet;
	
	private final int maxFollowSet;

	public HeadGrammarSlotArrayFirstFollow(int id, 
										   Nonterminal nonterminal, 
										   int nonterminalId, 
										   List<List<Symbol>> alts, 
										   Set<RegularExpression> followSet,
										   List<Set<RegularExpression>> predictionSets,
										   boolean nullable, 
										   int minPrediction, int maxPrediction, 
										   int minFollowSet, int maxFollowSet) {
		super(id, nonterminal, nonterminalId, alts, nullable);
		this.minPredictionSet = minPrediction;
		this.maxPredictionSet = maxPrediction;
		this.minFollowSet = minFollowSet;
		this.maxFollowSet = maxFollowSet;
		setPredictionSet(predictionSets);
		setFollowSet(followSet);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Set<Integer> set = predictionMap[lexer.getInput().charAt(ci) - minPredictionSet];
		
		for(int alternateIndex : set) {
			parser.addDescriptor(firstSlots[alternateIndex]);
		}
		
		return null;
	}
	
	@Override
	public boolean test(int v) {
		if(v < minPredictionSet || v > maxPredictionSet) {
			return false;
		}
		return predictionMap[v - minPredictionSet] != null;
	}
	
	@Override
	public boolean testFollowSet(int v) {
		if(v < minFollowSet || v > maxFollowSet) {
			return false;
		}
		return followSetMap[v - minFollowSet];
	}
	
	@SuppressWarnings("unchecked")
	private void setPredictionSet(List<Set<RegularExpression>> predictionSets) {
		
		predictionMap = new Set[maxPredictionSet - minPredictionSet + 1];
		
		for(int i = 0; i < firstSlots.length; i++) {
			Set<RegularExpression> predictionSet = predictionSets.get(i);
			
			if(predictionSet.isEmpty()) continue;
			
			for(RegularExpression regex : predictionSet) {
				for(Range r : regex.getFirstSet()) {
					for(int v = r.getStart(); v <= r.getEnd(); v++) {
						Set<Integer> set = predictionMap[v - minPredictionSet];
						if(set == null) {
							set = new HashSet<>();
							predictionMap[v - minPredictionSet] = set;
						}
						set.add(i);
					}
				}
			}
		}
	}
	
	private void setFollowSet(Set<RegularExpression> followSet) {
		followSetMap = new boolean[maxFollowSet - minFollowSet + 1];
		
		for(RegularExpression regex : followSet) {
			for(Range r : regex.getFirstSet()) {
				for(int v = r.getStart(); v <= r.getEnd(); v++) {
					followSetMap[v - minFollowSet] = true;
				}
			}
		}
		
	}

}
