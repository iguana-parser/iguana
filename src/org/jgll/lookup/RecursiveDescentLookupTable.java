package org.jgll.lookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class RecursiveDescentLookupTable extends DefaultLookup {

	private Stack<Descriptor> descriptorsStack;
	
	private Set<Descriptor> descriptorsSet;
	
	private TerminalSymbolNode[] terminals;
	
	private Map<NonPackedNode, NonPackedNode> nonPackedNodes;
	
	private int nonPackedNodesCount;
	
	public RecursiveDescentLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		descriptorsStack = new Stack<>();
		descriptorsSet = new HashSet<>();
		terminals = new TerminalSymbolNode[2 * inputSize];
		nonPackedNodes = new HashMap<NonPackedNode, NonPackedNode>(inputSize);
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !descriptorsStack.isEmpty();
	}

	@Override
	public Descriptor nextDescriptor() {
		return descriptorsStack.pop();
	}

	@Override
	public void addDescriptor(Descriptor descriptor) {
		if(descriptorsSet.contains(descriptor)) {
			return;
		}

		descriptorsStack.push(descriptor);
		descriptorsSet.add(descriptor);		
	}

	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		int index = 2 * leftExtent;
		if(terminalIndex != -2) {
			index = index + 1;
		}

		TerminalSymbolNode terminal = terminals[index];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			terminals[index] = terminal;
			nonPackedNodesCount++;
		}
		
		return terminal;
	}

	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key;
		if(slot.getId() < grammar.getNonterminals().size()) {
			key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}

		NonPackedNode value = nonPackedNodes.get(key);
		if(value == null) {
			value = key;
			nonPackedNodes.put(key, value);
		}
		
		return value;
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol) {
		return (NonterminalSymbolNode) nonPackedNodes.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int getNonPackedNodesCount() {
		return nonPackedNodesCount;
	}

	@Override
	public int getDescriptorsCount() {
		return descriptorsSet.size();
	}

}
