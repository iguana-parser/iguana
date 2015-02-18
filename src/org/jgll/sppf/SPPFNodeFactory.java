package org.jgll.sppf;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;

public class SPPFNodeFactory {

	private GrammarGraph grammarGraph;
	private PackedNodeSet set = (x, y) -> true;
	
	public SPPFNodeFactory(GrammarGraph grammarGraph) {
		this.grammarGraph = grammarGraph;
	}
	
	public NonterminalNode createNonterminalNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new NonterminalNode(grammarGraph.getHead(nonterminal), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createEpsilonNode(int inputIndex) {
		return createTerminalNode("epsilon", inputIndex, inputIndex);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(grammarGraph.getHead(nonterminal), leftExtent, rightExtent, set);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarGraph.getGrammarSlot(s), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createTerminalNode(String s, int leftExtent, int rightExtent) {
		return new TerminalNode(grammarGraph.getTerminal(grammarGraph.getRegularExpression(s)), leftExtent, rightExtent);
	}

	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getGrammarSlot(s), pivot, parent);
	}
	
}
