package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.LevelMap;
import org.jgll.util.hashing.LevelSet;
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
	
	private LevelSet<Descriptor> u;
	private LevelSet<SPPFNode> currentLevelNodes;
	
	private LevelSet<SPPFNode>[] forwardNodes;
	private LevelSet<Descriptor>[] forwardDescriptors;
	
	private Queue<Descriptor> r;
	private Queue<Descriptor>[] forwardRs;
	
	private LevelSet<GSSNode> currentGssNodes;
	private LevelSet<GSSNode>[] forwardGssNodes;
	
	private LevelSet<GSSEdge> currendEdges;
	private LevelSet<GSSEdge>[] forwardEdges;
	
	private LevelMap<GSSNode, Set<SPPFNode>> currentPoppedElements;
	private LevelMap<GSSNode, Set<SPPFNode>>[] forwardPoppedElements;
	
	private int countGSSNodes;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	private int packedNodesCount;
	
	protected int gssEdgesCount;
	
	@SuppressWarnings("unchecked")
	public LevelSynchronizedLookupTable(Grammar grammar, Input input) {
		super(grammar, input.size());
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new LevelSet<>(getSize());
		r = new ArrayDeque<>();
		
		forwardDescriptors = new LevelSet[longestTerminalChain];
		forwardRs = new Queue[longestTerminalChain];
		
		currentLevelNodes = new LevelSet<>();
		forwardNodes = new LevelSet[longestTerminalChain];
		
		currentGssNodes = new LevelSet<>();
		forwardGssNodes = new LevelSet[longestTerminalChain];
		
		currendEdges = new LevelSet<>();
		forwardEdges = new LevelSet[longestTerminalChain];
		
		currentPoppedElements = new LevelMap<>();
		forwardPoppedElements = new LevelMap[longestTerminalChain];
		
		for(int i = 0; i < longestTerminalChain; i++) {
			forwardDescriptors[i] = new LevelSet<>(getSize());
			forwardRs[i] = new ArrayDeque<>();
			forwardNodes[i] = new LevelSet<>();
			forwardGssNodes[i] = new LevelSet<>();
			forwardEdges[i] = new LevelSet<>();
			forwardPoppedElements[i] = new LevelMap<>();
		}
		
	}
	
	private void gotoNextLevel() {
		int nextIndex = indexFor(currentLevel + 1);
		
		LevelSet<Descriptor> tmpDesc = u;
		u.clear();
		u = forwardDescriptors[nextIndex];
		forwardDescriptors[nextIndex] = tmpDesc;
		
		Queue<Descriptor> tmpR = r;
		assert r.isEmpty();
		r = forwardRs[nextIndex];
		forwardRs[nextIndex] = tmpR;
		
		LevelSet<SPPFNode> tmp = currentLevelNodes;
		currentLevelNodes.clear();
		currentLevelNodes = forwardNodes[nextIndex];
		forwardNodes[nextIndex] = tmp;
		
		LevelSet<GSSNode> tmpGSSNodeSet = currentGssNodes;
		currentGssNodes.clear();
		currentGssNodes = forwardGssNodes[nextIndex];
		forwardGssNodes[nextIndex] = tmpGSSNodeSet;
		
		LevelSet<GSSEdge> tmpGSSEdgeSet = currendEdges;
		currendEdges.clear();
		currendEdges = forwardEdges[nextIndex];
		forwardEdges[nextIndex] = tmpGSSEdgeSet;
		
		LevelMap<GSSNode, Set<SPPFNode>> tmpPoppedElements = currentPoppedElements;
		currentPoppedElements.clear();
		currentPoppedElements = forwardPoppedElements[nextIndex];
		forwardPoppedElements[nextIndex] = tmpPoppedElements;

		
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
			value = currentLevelNodes.addAndGet(key);
			if(value == null){
				countNonPackedNodes++;
				newNodeCreated = true;
				value = key;
			}
		} else {
			int index = indexFor(rightExtent);
			value = forwardNodes[index].addAndGet(key);
			if(value == null){
				countNonPackedNodes++;
				newNodeCreated = true;
				value = key;
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
		
		CuckooHashSet<SPPFNode> currentNodes;
		
		if(currentLevel == inputSize - 1) {
			currentNodes = currentLevelNodes;
		} else {
			int index = indexFor(inputSize - 1); 
			currentNodes = forwardNodes[index];
		}
		
		return (NonterminalSymbolNode) currentNodes.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
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
		}
		else {
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
	public GSSNode getGSSNode(GrammarSlot label, int inputIndex) {
		GSSNode key = new GSSNode(label, inputIndex);
		GSSNode value;
		if(inputIndex == currentLevel) {
			value = currentGssNodes.addAndGet(key);
			if(value == null) {
				countGSSNodes++;
				value = key;
			}
		} else {
			int index = indexFor(inputIndex);
			value = forwardGssNodes[index].addAndGet(key);
			if(value == null) {
				countGSSNodes++;
				value = key;
			}
		}
		return value;
	}
	
	@Override
	public boolean hasGSSEdge(GSSNode source, SPPFNode label, GSSNode destination) {
		GSSEdge edge = new GSSEdge(source, label, destination);
		if(source.getInputIndex() == currentLevel) {
			boolean added = currendEdges.add(edge);
			if(added) {
				gssEdgesCount++;
				source.addGSSEdge(edge);
			}
			return !added;
		} 
		else {
			int index = indexFor(source.getInputIndex());
			boolean added = forwardEdges[index].add(edge);
			if(added) {
				gssEdgesCount++;
				source.addGSSEdge(edge);
			}
			return !added;
		}
	}

	@Override
	public int getGSSNodesCount() {
		return countGSSNodes;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addPackedNode(NonPackedNode parent, GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		if(parent.getCountPackedNode() == 0) {
			if(!leftChild.equals(DummyNode.getInstance())) {
				parent.addChild(leftChild);
			}
			parent.addChild(rightChild);
			parent.addFirstPackedNode(slot, pivot);
		}
		else if(parent.getCountPackedNode() == 1) {
			if(parent.getFirstPackedNodeGrammarSlot() == slot && parent.getFirstPackedNodePivot() == pivot) {
				return;
			} else {
				PackedNode packedNode = new PackedNode(slot, pivot, parent);
				PackedNode firstPackedNode = parent.addSecondPackedNode(packedNode, leftChild, rightChild);
				if(parent.getRightExtent() == currentLevel) {
					currentLevelNodes.add(packedNode);
					currentLevelNodes.add(firstPackedNode);
				} else {
					int index = indexFor(parent.getRightExtent());
					forwardNodes[index].add(packedNode);
					forwardNodes[index].add(firstPackedNode);
				}
				packedNodesCount += 2;
			}
		}
		else {
			PackedNode key = new PackedNode(slot, pivot, parent);
			if(parent.getRightExtent() == currentLevel) {
				if(currentLevelNodes.add(key)) {
					parent.addPackedNode(key, leftChild, rightChild);
					packedNodesCount++;
				}
			} else {
				int index = indexFor(parent.getRightExtent());
				if(forwardNodes[index].add(key)) {
					parent.addPackedNode(key, leftChild, rightChild);
					packedNodesCount++;
				}
			}
		}
	}

	@Override
	public int getPackedNodesCount() {
		return packedNodesCount;
	}

	@Override
	public int getGSSEdgesCount() {
		return gssEdgesCount;
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode) {
		if(gssNode.getInputIndex() == currentLevel) {
			Set<SPPFNode> set = currentPoppedElements.get(gssNode);
			if(set == null) {
				set = new HashSet<>();
				currentPoppedElements.put(gssNode, set);
			}
			set.add(sppfNode);
		} else {
			int index = indexFor(gssNode.getInputIndex());
			Set<SPPFNode> set = forwardPoppedElements[index].get(gssNode);
			if(set == null) {
				set = new HashSet<>();
			}
			set.add(sppfNode);
		}
	}

	@Override
	public Iterable<SPPFNode> getSPPFNodesOfPoppedElements(GSSNode gssNode) {
		if(gssNode.getInputIndex() == currentLevel) {
			Set<SPPFNode> set = currentPoppedElements.get(gssNode);
			if(set == null) {
				set = Collections.emptySet();
			}
			return set;
		} else {
			int index = indexFor(gssNode.getInputIndex());
			Set<SPPFNode> set = forwardPoppedElements[index].get(gssNode);
			if(set == null) {
				set = Collections.emptySet();
			}
			return set;
		}
	}


}
	
