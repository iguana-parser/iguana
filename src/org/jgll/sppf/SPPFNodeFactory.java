package org.jgll.sppf;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.ExternalHashEquals;

public class SPPFNodeFactory {

	private GrammarSlotRegistry registry;
	
	private ExternalHashEquals<NonPackedNode> hashEquals = NonPackedNode.globalHashEquals(HashFunctions.defaulFunction);
	
	public SPPFNodeFactory(GrammarSlotRegistry registry) {
		this.registry = registry;
	}
	
	public NonterminalNode createNonterminalNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new NonterminalNode(registry.getHead(nonterminal), leftExtent, rightExtent, hashEquals);
	}
	
	public TerminalNode createEpsilonNode(int inputIndex) {
		return new TerminalNode(Epsilon.TOKEN_ID, inputIndex, inputIndex, hashEquals);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(registry.getHead(nonterminal), leftExtent, rightExtent, hashEquals);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(registry.getGrammarSlot(s), leftExtent, rightExtent, hashEquals);
	}
	
	public TerminalNode createTerminalNode(String s, int leftExtent, int rightExtent) {
		return new TerminalNode(registry.getTerminal(registry.getRegularExpression(s)), leftExtent, rightExtent, hashEquals);
	}

	public ListSymbolNode createListNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new ListSymbolNode(registry.getHead(nonterminal), leftExtent, rightExtent, hashEquals);
	}
	
	public ListSymbolNode createListNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new ListSymbolNode(registry.getHead(nonterminal), leftExtent, rightExtent, hashEquals);
	}
	
	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(registry.getGrammarSlot(s), pivot, parent);
	}
	
}
