package org.jgll.sppf;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Nonterminal;

public class SPPFNodeFactory {

	private GrammarRegistry registry;
	private PackedNodeSet set = (x, y) -> true;
	
	public SPPFNodeFactory(GrammarRegistry registry) {
		this.registry = registry;
	}
	
	public NonterminalNode createNonterminalNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new NonterminalNode(registry.getHead(nonterminal), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createEpsilonNode(int inputIndex) {
		return createTerminalNode("epsilon", inputIndex, inputIndex);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(registry.getHead(nonterminal), leftExtent, rightExtent, set);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(registry.getGrammarSlot(s), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createTerminalNode(String s, int leftExtent, int rightExtent) {
		return new TerminalNode(registry.getTerminal(registry.getRegularExpression(s)), leftExtent, rightExtent);
	}

	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(registry.getGrammarSlot(s), pivot, parent);
	}
	
}
