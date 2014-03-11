package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

public class LL1HeadGrammarSlot extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Set<BodyGrammarSlot>[] predictionMap;
	
	private int min;
	
	private int max;

	public LL1HeadGrammarSlot(Nonterminal nonterminal, int nonterminalId, Set<List<Symbol>> alternates, boolean nullable, int min, int max) {
		super(nonterminal, nonterminalId, alternates, nullable);
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
			
			ntNode.addFirstPackedNode(lastSlot.getNodeId(), ci);
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

}
