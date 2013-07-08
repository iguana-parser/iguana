package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.DescriptorSet;
import org.jgll.util.hashing.SPPFNodeSet;

/**
 * 
 * Provides lookup functionality for the level-based processing of the input
 * in a GLL parser. 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class LevelSynchronizedLookupTable extends AbstractLookupTable {
	
	private int currentLevel;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[][] terminals;
	
	private DescriptorSet u;
	
	private SPPFNodeSet currentLevelNonPackedNodes;
	
	private SPPFNodeSet[] forwardNonPackedNodes;
	
	private DescriptorSet[] forwardDescriptors;
	
	private Queue<Descriptor> r;
	
	private Queue<Descriptor>[] forwardRs;
	
	private Set<LastGrammarSlot> poppedSlots;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	@SuppressWarnings("unchecked")
	public LevelSynchronizedLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new DescriptorSet(getSize());
		r = new ArrayDeque<>();
		
		forwardDescriptors = new DescriptorSet[longestTerminalChain];
		forwardRs = new Queue[longestTerminalChain];
		
		poppedSlots = new CuckooHashSet<>();
		
		currentLevelNonPackedNodes = new SPPFNodeSet();
		forwardNonPackedNodes = new SPPFNodeSet[longestTerminalChain];

		
		for(int i = 0; i < longestTerminalChain; i++) {
			forwardDescriptors[i] = new DescriptorSet(getSize());
			forwardRs[i] = new ArrayDeque<>();
			forwardNonPackedNodes[i] = new SPPFNodeSet();
		}
	}
	
	private void gotoNextLevel() {
		int nextIndex = indexFor(currentLevel + 1);
		
		DescriptorSet tmpDesc = u;
		u.clear();
		u = forwardDescriptors[nextIndex];
		forwardDescriptors[nextIndex] = tmpDesc;
		
		Queue<Descriptor> tmpR = r;
		assert r.isEmpty();
		r = forwardRs[nextIndex];
		forwardRs[nextIndex] = tmpR;
		
		SPPFNodeSet tmp = currentLevelNonPackedNodes;
		currentLevelNonPackedNodes.clear();
		currentLevelNonPackedNodes = forwardNonPackedNodes[nextIndex];
		forwardNonPackedNodes[nextIndex] = tmp;
		
		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;
		
		poppedSlots.clear();

		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % longestTerminalChain;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		SPPFNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		
		if(rightExtent == currentLevel) {
			SPPFNode value = currentLevelNonPackedNodes.get(key);
			if(value == null) {
				value = key;
				currentLevelNonPackedNodes.add(value);
				countNonPackedNodes++;
			}
			return value;
		} else {
			int index = indexFor(rightExtent);
			
			SPPFNode value = forwardNonPackedNodes[index].get(key);
			if(value == null) {
				value = key;
				forwardNonPackedNodes[index].add(value);
				countNonPackedNodes++;
			}
			return value;
		}
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
		CuckooHashSet<SPPFNode> set;
		if(currentLevel == inputSize - 1) {
			set = currentLevelNonPackedNodes;
		} else {
			int index = indexFor(inputSize - 1); 
			set = forwardNonPackedNodes[index];
		}
		return (NonterminalSymbolNode) set.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
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
		if(!r.isEmpty()) {
			size--;
			return r.remove();
		} else {
			gotoNextLevel();
			return nextDescriptor();
		}
	}
	
	private int getSize() {
		return grammar.getMaxDescriptorsAtInput();
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		int inputIndex = descriptor.getInputIndex();
		if(inputIndex == currentLevel) {
			if(u.add(descriptor)) {
				 r.add(descriptor);
				 size++;
				 all++;
			} else {
				return false;
			}
		}
		
		else {
			int index = indexFor(descriptor.getInputIndex());
			if(forwardDescriptors[index].add(descriptor)) {
				forwardRs[index].add(descriptor);
				size++;
				all++;
			}  else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int getDescriptorsCount() {
		return all;
	}

	@Override
	public boolean isPopped(LastGrammarSlot slot) {
		return poppedSlots.contains(slot);
	}

	@Override
	public void setPopped(LastGrammarSlot slot) {
		poppedSlots.add(slot);
	}

	@Override
	public void clearPopped(LastGrammarSlot slot) {
		poppedSlots.remove(slot);
	}

}