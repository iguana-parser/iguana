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
	
	private Set<Integer>[] predictionMap;
	
	private int min;
	
	private int max;

	public HeadGrammarSlotArrayFirstFollow(Nonterminal nonterminal, Set<List<Symbol>> alts, List<Set<RegularExpression>> predictionSets, boolean nullable, int min, int max) {
		super(nonterminal, alts, nullable);
		this.min = min;
		this.max = max;
		setPredictionSet(predictionSets);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Set<Integer> set = predictionMap[lexer.getInput().charAt(ci) - min];
		
		for(int alternateIndex : set) {
			parser.addDescriptor(alternates.get(alternateIndex).getFirstSlot());
		}
		
		return null;
	}
	
	@Override
	public boolean test(int v) {
		if(v < min || v > max) {
			return false;
		}
		return predictionMap[v - min] != null;
	}
	
	@SuppressWarnings("unchecked")
	private void setPredictionSet(List<Set<RegularExpression>> predictionSets) {
		
		predictionMap = new Set[max - min + 1];
		
		for(int i = 0; i < alts.size(); i++) {
			for(RegularExpression regex : predictionSets.get(i)) {
				for(Range r : regex.getFirstSet()) {
					for(int v = r.getStart(); v <= r.getEnd(); v++) {
						Set<Integer> set = predictionMap[v - min];
						if(set == null) {
							set = new HashSet<>();
							predictionMap[v - min] = set;
						}
						set.add(i);
					}
				}
			}
 		}
	}

}
