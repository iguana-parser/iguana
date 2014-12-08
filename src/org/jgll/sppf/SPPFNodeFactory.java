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
		return new NonterminalNode(grammarGraph.getResolver().getHead(nonterminal), leftExtent, rightExtent);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(grammarGraph.getResolver().getHead(nonterminal), leftExtent, rightExtent);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarGraph.getResolver().getGrammarSlot(s), leftExtent, rightExtent);
	}
	
	public TokenSymbolNode createTokenNode(String s, int leftExtent, int rightExtent) {
		return new TokenSymbolNode(grammarGraph.getResolver().getRegularExpression(s), leftExtent, rightExtent - leftExtent);
	}

	public ListSymbolNode createListNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new ListSymbolNode(grammarGraph.getHeadGrammarSlot(nonterminal), leftExtent, rightExtent);
	}
	
	public ListSymbolNode createListNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new ListSymbolNode(grammarGraph.getHeadGrammarSlot(nonterminal), leftExtent, rightExtent);
	}
	
	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getGrammarSlotByName(s), pivot, parent);
	}
	
}
