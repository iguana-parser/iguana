package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.hashing.OpenAddressingHashSet;

/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("unchecked")
public class LevelSynchronizedLookupTable extends AbstractLookupTable {

	private int currentLevel;
	
	private Map<SPPFNode, SPPFNode>[] levels;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[][] terminals;
	
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
	
	public LevelSynchronizedLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		levels = new Map[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			levels[i] = new HashMap<>();
		}
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new OpenAddressingHashSet[longestTerminalChain + 1];
		r = new Queue[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			r[i] = new ArrayDeque<>();
		}
		
		u[0] = new OpenAddressingHashSet<>(grammar.getMaxDescriptorsAtInput());
	}
	
	private int sum(int index) {
		int sum = 0;
		for(int i = 0; i < u.length; i++) {
			if(i == index || u[i] == null) {
				continue;
			}
			sum += u[i].size();
		}
		return sum;
	}
	
	private void nextLevel() {
		levels[indexFor(currentLevel)] = new HashMap<>();
		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % (longestTerminalChain + 1);
	}
	
	private int previousIndex(int inputIndex) {
		int index = indexFor(inputIndex);
		if(index == -1) {
			index = longestTerminalChain;
		}
		return index;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {

		SPPFNode key = createNonPackedNode(slot, leftExtent, rightExtent);		
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
		
		int index2;
		int rightExtent;
		if(terminalIndex == -2) {
			rightExtent = leftExtent;
			index2 = 1;
		} else {
			rightExtent = leftExtent + 1;
			index2 = 0;
		}
		
		int index = indexFor(rightExtent);

		TerminalSymbolNode terminal = terminals[index][index2];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			countNonPackedNodes++;
			terminals[index][index2] = terminal;
		}
		
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol) {
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
			u[index] = new OpenAddressingHashSet<>(getSize(index));
			nextLevel();
			currentLevel++;
			return nextDescriptor();
		}
	}
	
	private int getSize(int index) {
		int size = sum(index) + (u[previousIndex(currentLevel)] == null ? 1 : u[previousIndex(currentLevel)].size()) * grammar.getMaxDescriptorsAtInput();
		System.out.println(currentLevel + ", " + size);
		return size;
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		int index = indexFor(descriptor.getInputIndex());
		
		if(u[index] == null) {
			u[index] = new OpenAddressingHashSet<>(getSize(0));
		}
		
		if(! u[index].contains(descriptor)) {
			 r[index].add(descriptor);
			 u[index].add(descriptor);
			 size++;
			 all++;
			 return true;
		}
		
		return false;
	}

	@Override
	public int getDescriptorsCount() {
		return all;
	}

}