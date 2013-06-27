package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.SparseBitSet;

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
	
	private SparseBitSet[] u;
	
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
		
		u = new SparseBitSet[longestTerminalChain + 1];
		r = new Queue[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			u[i] = new SparseBitSet();
			r[i] = new ArrayDeque<>();
		}
	}
	
	private void nextLevel() {
		levels[indexFor(currentLevel)] = new HashMap<>();
		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % (longestTerminalChain + 1);
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
			u[index] = new SparseBitSet();
			nextLevel();
			currentLevel++;
			return nextDescriptor();
		}
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		int index = indexFor(descriptor.getInputIndex());
		
		long i = indexForDescriptor(descriptor);
		if(! u[index].get(i)) {
			 r[index].add(descriptor);
			 u[index].set(i);
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
	
	private long indexForDescriptor(Descriptor desc) {
		
		int countSlots = grammar.getGrammarSlots().size();
		
		long firstDim = inputSize;
		long secondDim = countSlots * firstDim;
		long thirdDim = countSlots * secondDim;
		
		return desc.getSPPFNode().getLeftExtent() +
			   desc.getLabel().getId() * firstDim +
			   desc.getSPPFNode().getGrammarSlot().getId() * secondDim + 
			   desc.getGSSNode().getGrammarSlot().getId() * thirdDim;
	}

	@Override
	public boolean removeDescriptor(Descriptor descriptor) {
		// TODO Auto-generated method stub
		return false;
	}

}
