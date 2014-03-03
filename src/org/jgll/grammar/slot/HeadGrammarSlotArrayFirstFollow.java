package org.jgll.grammar.slot;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class HeadGrammarSlotArrayFirstFollow extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Set<BodyGrammarSlot>[] predictionMap;
	
	private int min;
	
	private int max;

	public HeadGrammarSlotArrayFirstFollow(Nonterminal nonterminal, int min, int max) {
		super(nonterminal);
		this.min = min;
		this.max = max;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		if(lexer.getInput().charAt(ci) < min) {
			System.out.println("WTF?");
		}
		
		Set<BodyGrammarSlot> set = predictionMap[lexer.getInput().charAt(ci) - min];
		
		if(set != null) {
			for(BodyGrammarSlot slot : set) {
				parser.addDescriptor(slot);
			}
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
	@Override
	public void setPredictionSet() {
		
		predictionMap = new Set[max - min + 1];
		
		for(Alternate alt : alternates) {
			for(RegularExpression regex : alt.getPredictionSet()) {
				for(Range r : regex.getFirstSet()) {
					for(int i = r.getStart(); i <= r.getEnd(); i++) {
						Set<BodyGrammarSlot> set = predictionMap[i - min];
						if(set == null) {
							set = new HashSet<>();
							predictionMap[i - min] = set;
						}
						set.add(alt.getFirstSlot());
					}
				}
			}
 		}
	}

}
