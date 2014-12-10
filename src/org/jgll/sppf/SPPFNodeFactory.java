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
		return new NonterminalNode(grammarGraph.getRegistry().getHead(nonterminal), leftExtent, rightExtent);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(grammarGraph.getRegistry().getHead(nonterminal), leftExtent, rightExtent);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarGraph.getRegistry().getGrammarSlot(s), leftExtent, rightExtent);
	}
	
	public TerminalSymbolNode createTokenNode(String s, int leftExtent, int rightExtent) {
		return new TerminalSymbolNode(grammarGraph.getRegistry().getRegularExpression(s), leftExtent, rightExtent);
	}

	public ListSymbolNode createListNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new ListSymbolNode(grammarGraph.getRegistry().getHead(nonterminal), leftExtent, rightExtent);
	}
	
	public ListSymbolNode createListNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new ListSymbolNode(grammarGraph.getRegistry().getHead(nonterminal), leftExtent, rightExtent);
	}
	
	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getRegistry().getGrammarSlot(s), pivot, parent);
	}
	
}
