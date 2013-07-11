package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.grammar.PopUnit;
import org.jgll.parser.AbstractGLLParser;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.DescriptorSet;
import org.jgll.util.hashing.SPPFNodeSet;
import org.jgll.util.logging.LoggerWrapper;

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
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(LevelSynchronizedLookupTable.class);
	
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
	
	private Map<LastGrammarSlot, PopUnit> poppedSlots;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;

	private GLLParser parser;

	private Input input;
	
	@SuppressWarnings("unchecked")
	public LevelSynchronizedLookupTable(GLLParser parser, Grammar grammar, Input input) {
		super(grammar, input.size());
		this.parser = parser;
		this.input = input;
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new DescriptorSet(getSize());
		r = new ArrayDeque<>();
		
		forwardDescriptors = new DescriptorSet[longestTerminalChain];
		forwardRs = new Queue[longestTerminalChain];
		
		poppedSlots = new HashMap<>();
		
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
		
		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % longestTerminalChain;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		boolean newNodeCreated = false;
		SPPFNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		SPPFNode value;
		
		if(rightExtent == currentLevel) {
			value = currentLevelNonPackedNodes.get(key);
			if(value == null) {
				value = key;
				currentLevelNonPackedNodes.add(value);
				countNonPackedNodes++;
				newNodeCreated = true;
			}
		} else {
			int index = indexFor(rightExtent);
			value = forwardNonPackedNodes[index].get(key);
			if(value == null) {
				value = key;
				forwardNonPackedNodes[index].add(value);
				countNonPackedNodes++;
				newNodeCreated = true;
			}
		}
		
		log.trace("SPPF node created: %s : %b", value, newNodeCreated);
		return value;
	}
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		
		boolean newNodeCreated = false;
		
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
			newNodeCreated = true;
		}
		
		log.trace("SPPF Terminal node created: %s : %b", terminal, newNodeCreated);
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol) {
		
		while(!poppedSlots.isEmpty()) {
			for(PopUnit popUnit : poppedSlots.values()) {
				((AbstractGLLParser) parser).forcedPop(popUnit.getGssNode(), popUnit.getInputIndex(), popUnit.getSppfNode());
			}
			poppedSlots.clear();
			L0.getInstance().parse(parser, input);
		}
		
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
		} else if(!poppedSlots.isEmpty()) {
			for(PopUnit popUnit : poppedSlots.values()) {
				((AbstractGLLParser) parser).forcedPop(popUnit.getGssNode(), popUnit.getInputIndex(), popUnit.getSppfNode());
			}
			poppedSlots.clear();
			return nextDescriptor();
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
		return poppedSlots.containsKey(slot);
	}

	@Override
	public void addPopped(LastGrammarSlot slot, PopUnit popUnit) {
		poppedSlots.put(slot, popUnit);
	}

	@Override
	public void clearPopped(LastGrammarSlot slot) {
		poppedSlots.remove(slot);
	}
	
}