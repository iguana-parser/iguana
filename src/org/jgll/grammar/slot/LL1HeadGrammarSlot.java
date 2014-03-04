package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

public class LL1HeadGrammarSlot extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Set<BodyGrammarSlot>[] predictionMap;
	
	private int min;
	
	private int max;

	public LL1HeadGrammarSlot(Nonterminal nonterminal, int min, int max) {
		super(nonterminal);
		this.min = min;
		this.max = max;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		List<SPPFNode> children = new ArrayList<>();
		
		Set<BodyGrammarSlot> set = null; // predictionMap.get(lexer.getInput().charAt(ci));
		
		if(set == null || set.isEmpty()) {
			return null;
		}
		
		if(set.size() > 1) {
			System.out.println(nonterminal.getName());
		}
		assert set.size() == 1;
		
		BodyGrammarSlot currentSlot = set.iterator().next();
		
		LastGrammarSlot lastSlot = null;
		
		while(!(currentSlot instanceof LastGrammarSlot)) {
//			SPPFNode node = currentSlot.parse(parser, lexer);
			SPPFNode node  = null;
			if(node == null) {
				return null;
			}
			children.add(node);
			currentSlot = currentSlot.next();
		}
		
		lastSlot = (LastGrammarSlot) currentSlot;

		int leftExtent;
		int rightExtent;
		
		if(children.size() == 0) {
			leftExtent = parser.getCurrentInputIndex();
			rightExtent = leftExtent;
		}
		else if(children.size() == 1) {
			leftExtent = children.get(0).getLeftExtent();
			rightExtent = children.get(0).getRightExtent();
		} else {
			leftExtent = children.get(0).getLeftExtent();
			rightExtent = children.get(children.size() - 1).getRightExtent();
		}

		NonterminalSymbolNode ntNode = parser.getSPPFLookup().findNonterminalNode(this, leftExtent, rightExtent);
		
		if(ntNode == null) {
			ntNode = parser.getSPPFLookup().getNonterminalNode(this, leftExtent, rightExtent); 
			
			for(SPPFNode node : children) {
				ntNode.addChild(node);
			}
			
			ntNode.addFirstPackedNode(lastSlot, ci);
		}
		
//		return ntNode;
		return null;
	}
	
	@Override
	public boolean test(int v) {
		if(v < min || v > max) {
			return false;
		}
		return predictionMap[v] != null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setPredictionSet() {
		
		predictionMap = new Set[max - min + 2];
		
		for(Alternate alt : alternates) {
			for(RegularExpression regex : alt.getPredictionSet()) {
				for(Range r : regex.getFirstSet()) {
					Set<BodyGrammarSlot> s1 = predictionMap[r.getStart() - min];
					if(s1 == null) {
						s1 = new HashSet<>();
						predictionMap[r.getStart() - min] =  s1;
					}
					s1.add(alt.getFirstSlot());
					
					Set<BodyGrammarSlot> s2 = predictionMap[r.getEnd() + 1 - min];
					if(s2 == null) {
						predictionMap[r.getEnd() + 1 - min] =  null;						
					}
				}
			}
 		}
	}

}
