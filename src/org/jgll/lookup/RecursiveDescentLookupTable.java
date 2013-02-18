package org.jgll.lookup;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class RecursiveDescentLookupTable extends DefaultLookup {

	private Stack<Descriptor> stack;
	
	private Set<Descriptor> set;
	
	public RecursiveDescentLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		stack = new Stack<>();
		set = new HashSet<>();
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !stack.isEmpty();
	}

	@Override
	public Descriptor nextDescriptor() {
		return stack.pop();
	}

	@Override
	public void addDescriptor(Descriptor descriptor) {
		if(set.contains(descriptor)) {
			return;
		}

		stack.push(descriptor);
		set.add(descriptor);		
	}

	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		return null;
	}

	@Override
	public SPPFNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return null;
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol) {
		return null;
	}

	@Override
	public int getNonPackedNodesCount() {
		return 0;
	}

	@Override
	public int getDescriptorsCount() {
		return set.size();
	}

}
