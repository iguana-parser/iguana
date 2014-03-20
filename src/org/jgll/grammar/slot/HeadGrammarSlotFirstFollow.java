package org.jgll.grammar.slot;

import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.test.FollowTest;
import org.jgll.grammar.slot.test.PredictionTest;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class HeadGrammarSlotFirstFollow extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
		
	private final PredictionTest predictionTest;
	
	private final FollowTest followTest;

	public HeadGrammarSlotFirstFollow(int id, 
									  Nonterminal nonterminal, 
									  int nonterminalId, 
									  List<List<Symbol>> alts, 
									  PredictionTest predictiTest,
									  FollowTest followTest,
									  boolean nullable) {
		super(id, nonterminal, nonterminalId, alts, nullable);
		this.predictionTest = predictiTest;
		this.followTest = followTest;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Set<Integer> set = predictionTest.get(lexer.getInput().charAt(ci));
		
		if(set == null) return null;
		
		for(int alternateIndex : set) {
			parser.addDescriptor(firstSlots[alternateIndex]);
		}
		
		return null;
	}
	
	@Override
	public boolean test(int v) {
		return predictionTest.test(v);
	}
	
	@Override
	public boolean testFollowSet(int v) {
		return followTest.test(v);
	}
	
}
