package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("unchecked")
public class MapLevelledLookup extends DefaultLookup implements LevelledLookup {

	private int currentLevel;
	
	private Map<SPPFNode, SPPFNode>[] levels;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[] terminals;
	
	private Set<Descriptor>[] u;
	
	private Queue<Descriptor>[] r;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	public MapLevelledLookup(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		levels = new Map[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			levels[i] = new HashMap<>();
		}
		
		terminals = new TerminalSymbolNode[2 * inputSize];
		
		u = new Set[longestTerminalChain + 1];
		r = new Queue[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			u[i] = new HashSet<>();
			r[i] = new ArrayDeque<>();
		}

	}
	
	@Override
	public void nextLevel() {
		levels[indexFor(currentLevel)] = new HashMap<>();
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % (longestTerminalChain + 1);
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {

		SPPFNode key;
		if(slot.getId() < grammar.getNonterminals().size()) {
			key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		
		int index = indexFor(rightExtent);
			SPPFNode value = levels[index].get(key);
			if(value == null) {
				value = key;
				levels[index].put(key, value);
				countNonPackedNodes++;
			}			
			return value;
	}
	
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		int index = 2 * leftExtent;
		if(terminalIndex != -2) {
			index = 2 * leftExtent + 1;
		}

		TerminalSymbolNode terminal = terminals[index];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			countNonPackedNodes++;
			terminals[index] = terminal;
		}
		
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol) {
		int index = indexFor(inputSize - 1); 
		if(levels[index] == null) {
			return null;
		}
		return (NonterminalSymbolNode) levels[index].get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int getNonPackedNodesCount() {
		return countNonPackedNodes;
	}

	@Override
	public boolean hasNextDescriptor() {
		return size > 0;
	}

	@Override
	public Descriptor nextDescriptor() {
		int index = indexFor(currentLevel); 
		if(!r[index].isEmpty()) {
			size--;
			return r[index].remove();
		} else {
			u[index] = new HashSet<>();			
			nextLevel();
			currentLevel++;
			return nextDescriptor();
		}
	}

	@Override
	public void addDescriptor(Descriptor descriptor) {
		int index = indexFor(descriptor.getInputIndex());
		
		if(! u[index].contains(descriptor)) {
			 r[index].add(descriptor);
			 u[index].add(descriptor);
			 size++;
			 all++;
		}		
	}

	@Override
	public int getDescriptorsCount() {
		return all;
	}

}
