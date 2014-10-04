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
		return new NonterminalNode(grammarGraph.getNonterminalId(nonterminal), leftExtent, rightExtent);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(grammarGraph.getNonterminalId(nonterminal), leftExtent, rightExtent);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		int id = grammarGraph.getGrammarSlotByName(s).getId();
		return new IntermediateNode(id, leftExtent, rightExtent);
	}
	
	public TokenSymbolNode createTokenNode(String s, int leftExtent, int rightExtent) {
		return new TokenSymbolNode(grammarGraph.getRegularExpressionId(grammarGraph.getRegularExpressionByName(s)), leftExtent, rightExtent - leftExtent);
	}

	public ListSymbolNode createListNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new ListSymbolNode(grammarGraph.getNonterminalId(nonterminal), leftExtent, rightExtent);
	}
	
	public ListSymbolNode createListNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new ListSymbolNode(grammarGraph.getNonterminalId(nonterminal), leftExtent, rightExtent);
	}
	
	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getGrammarSlotByName(s).getId(), pivot, parent);
	}
	
}
