package org.jgll.grammar.slot;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class HeadGrammarSlotWithFirstFollowCheck extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Map<Integer, Set<BodyGrammarSlot>> predictionMap;

	public HeadGrammarSlotWithFirstFollowCheck(Nonterminal nonterminal) {
		super(nonterminal);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Set<BodyGrammarSlot> set = predictionMap.get(lexer.getInput().charAt(ci));

		if(set != null) {
			for(BodyGrammarSlot slot : set) {
				parser.addDescriptor(slot);
			}
		}
		
		return null;
	}
	
	public void setPredictionSet(Set<Integer> predictionSet, List<RegularExpression> regularExpressions) {
		
//		predictionMap = new HashMap<>();
//
//		for(int i = 0; i < alternates.size(); i++) {
//			
//			final Alternate alternate = alternates.get(i);
//			
//			Set<Integer> set = alternate.getPredictionSet();
//
//			for (int j : set) {
//				RegularExpression regex = regularExpressions.get(j);
//				for(int k : regex.getFirstSet()) {
//					Set<BodyGrammarSlot> s = predictionMap.get(k);
//					if(s == null) {
//						s = new HashSet<>();
//						predictionMap.put(k, s);
//					}
//					s.add(alternate.getFirstSlot());					
//				}
//			}
//		}
		
	}


}
