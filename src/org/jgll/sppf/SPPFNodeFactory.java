package org.jgll.sppf;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;

public class SPPFNodeFactory {

	private GrammarGraph grammarGraph;

	public SPPFNodeFactory(GrammarGraph grammarGraph) {
		this.grammarGraph = grammarGraph;
	}
	
	public NonterminalNode createNonterminalNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new NonterminalNode(grammarGraph.getNonterminalId(nonterminal), 
										 grammarGraph.getCountAlternates(nonterminal), 
										 leftExtent, 
										 rightExtent);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		int id = grammarGraph.getGrammarSlotByName(s).getId();
		return new IntermediateNode(id, leftExtent, rightExtent);
	}
	
	public TokenSymbolNode createTokenNode(String s, int leftExtent, int rightExtent) {
		return new TokenSymbolNode(grammarGraph.getRegularExpressionId(grammarGraph.getRegularExpressionByName(s)), leftExtent, rightExtent - leftExtent);
	}
	
	public ListSymbolNode createListNode(Nonterminal nonterminal, int leftExtent, int rightExtent) {
		return new ListSymbolNode(grammarGraph.getNonterminalId(nonterminal), 
				 grammarGraph.getCountAlternates(nonterminal), 
				 leftExtent, 
				 rightExtent);
	}
	
	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getGrammarSlotByName(s).getId(), pivot, parent);
	}
	
}
