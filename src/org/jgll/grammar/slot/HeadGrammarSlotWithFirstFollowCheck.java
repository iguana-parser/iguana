package org.jgll.grammar.slot;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class HeadGrammarSlotWithFirstFollowCheck extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private NavigableMap<Integer, Set<BodyGrammarSlot>> predictionMap;

	public HeadGrammarSlotWithFirstFollowCheck(Nonterminal nonterminal) {
		super(nonterminal);
		predictionMap = new TreeMap<>();
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Entry<Integer, Set<BodyGrammarSlot>> e = predictionMap.floorEntry(lexer.getInput().charAt(ci));
		if(e != null) {
			Set<BodyGrammarSlot> set = e.getValue();

			if(set != null) {
				for(BodyGrammarSlot slot : set) {
					parser.addDescriptor(slot);
				}
			}			
		}
		
		return null;
	}
	
	public void setPredictionSet(Set<Integer> predictionSet, List<RegularExpression> regularExpressions) {
		
		predictionMap = new TreeMap<>();

		for(Alternate alt : alternates) {
			for(int i : alt.getPredictionSet()) {
				RegularExpression regex = regularExpressions.get(i);
				for(Range r : regex.getFirstSet()) {
					Set<BodyGrammarSlot> s1 = predictionMap.get(r.getStart());
					if(s1 == null) {
						s1 = new HashSet<>();
						predictionMap.put(r.getStart(), s1);
					}
					s1.add(alt.getFirstSlot());
					
					predictionMap.put(r.getEnd() + 1, null);
				}
			}
 		}
	}


}
