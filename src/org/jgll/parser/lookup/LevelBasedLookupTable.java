package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
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
import org.jgll.util.hashing.CuckooHashMap;
import org.jgll.util.hashing.CuckooHashSet;
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
public class LevelBasedLookupTable extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(LevelBasedLookupTable.class);
	
	private int currentLevel;
	
	private int countNonPackedNodes;

	private int chainLength;
	
	private TerminalSymbolNode[][] terminals;
	
	private CuckooHashSet<Descriptor> u;
	private CuckooHashSet<Descriptor>[] forwardDescriptors;

	private CuckooHashSet<NonPackedNode> currentNodes;
	private CuckooHashSet<NonPackedNode>[] forwardNodes;
	
	private CuckooHashSet<PackedNode> currentPackedNodes;
	private CuckooHashSet<PackedNode>[] forwardPackedNodes;
	
	private Queue<Descriptor> r;
	private Queue<Descriptor>[] forwardRs;
	
	private CuckooHashSet<GSSNode> currentGssNodes;
	private CuckooHashSet<GSSNode>[] forwardGssNodes;
	
	private CuckooHashSet<GSSEdge> currendEdges;
	private CuckooHashSet<GSSEdge>[] forwardEdges;
	
	private CuckooHashMap<GSSNode, Set<SPPFNode>> currentPoppedElements;
	private CuckooHashMap<GSSNode, Set<SPPFNode>>[] forwardPoppedElements;
	
	private int countGSSNodes;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	private int countPackedNodes;
	
	protected int countGSSEdges;
	
	private final int initialSize = 2048;
	
	public LevelBasedLookupTable(Grammar grammar) {
		this(grammar, grammar.getLongestTerminalChain());
	}
	
	@SuppressWarnings("unchecked")
	public LevelBasedLookupTable(Grammar grammar, int chainLength) {
		super(grammar);
		
		this.chainLength = chainLength;
		terminals = new TerminalSymbolNode[chainLength + 1][2];
		
		u = new CuckooHashSet<>(getSize(), Descriptor.levelBasedExternalHasher);
		r = new ArrayDeque<>();
		
		forwardDescriptors = new CuckooHashSet[chainLength];
		forwardRs = new Queue[chainLength];
		
		currentNodes = new CuckooHashSet<>(initialSize, NonPackedNode.levelBasedExternalHasher);
		forwardNodes = new CuckooHashSet[chainLength];
		
		currentPackedNodes = new CuckooHashSet<>(initialSize, PackedNode.levelBasedExternalHasher);
		forwardPackedNodes = new CuckooHashSet[chainLength];
		
		currentGssNodes = new CuckooHashSet<>(initialSize, GSSNode.levelBasedExternalHasher);
		forwardGssNodes = new CuckooHashSet[chainLength];
		
		currendEdges = new CuckooHashSet<>(initialSize, GSSEdge.levelBasedExternalHasher);
		forwardEdges = new CuckooHashSet[chainLength];
		
		currentPoppedElements = new CuckooHashMap<>(initialSize, GSSNode.levelBasedExternalHasher);
		forwardPoppedElements = new CuckooHashMap[chainLength];
		
		for(int i = 0; i < chainLength; i++) {
			forwardDescriptors[i] = new CuckooHashSet<>(getSize(), Descriptor.levelBasedExternalHasher);
			forwardRs[i] = new ArrayDeque<>(initialSize);
			forwardNodes[i] = new CuckooHashSet<>(initialSize, NonPackedNode.levelBasedExternalHasher);
			forwardGssNodes[i] = new CuckooHashSet<>(initialSize, GSSNode.levelBasedExternalHasher);
			forwardEdges[i] = new CuckooHashSet<>(initialSize, GSSEdge.levelBasedExternalHasher);
			forwardPoppedElements[i] = new CuckooHashMap<>(initialSize, GSSNode.levelBasedExternalHasher);
			forwardPackedNodes[i] = new CuckooHashSet<>(PackedNode.levelBasedExternalHasher);
		}
	}
	
	private void gotoNextLevel() {
		int nextIndex = indexFor(currentLevel + 1);
		
		CuckooHashSet<Descriptor> tmpDesc = u;
		u.clear();
		u = forwardDescriptors[nextIndex];
		forwardDescriptors[nextIndex] = tmpDesc;
		
		Queue<Descriptor> tmpR = r;
		assert r.isEmpty();
		r = forwardRs[nextIndex];
		forwardRs[nextIndex] = tmpR;
		
		CuckooHashSet<NonPackedNode> tmpNonPackedNode = currentNodes;
		currentNodes.clear();
		currentNodes = forwardNodes[nextIndex];
		forwardNodes[nextIndex] = tmpNonPackedNode;
		
		CuckooHashSet<PackedNode> tmpPackedNode = currentPackedNodes;
		currentPackedNodes.clear();
		currentPackedNodes = forwardPackedNodes[nextIndex];
		forwardPackedNodes[nextIndex] = tmpPackedNode;
		
		CuckooHashSet<GSSNode> tmpGSSNodeSet = currentGssNodes;
		currentGssNodes.clear();
		currentGssNodes = forwardGssNodes[nextIndex];
		forwardGssNodes[nextIndex] = tmpGSSNodeSet;
		
		CuckooHashSet<GSSEdge> tmpGSSEdgeSet = currendEdges;
		currendEdges.clear();
		currendEdges = forwardEdges[nextIndex];
		forwardEdges[nextIndex] = tmpGSSEdgeSet;
		
		CuckooHashMap<GSSNode, Set<SPPFNode>> tmpPoppedElements = currentPoppedElements;
		currentPoppedElements.clear();
		currentPoppedElements = forwardPoppedElements[nextIndex];
		forwardPoppedElements[nextIndex] = tmpPoppedElements;

		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;
		
		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % chainLength;
	}
	
	@Override
	public NonPackedNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		return getNonPackedNode(key);
	}
	
	@Override
	public NonPackedNode getNonPackedNode(NonPackedNode key) {
		boolean newNodeCreated = false;
		NonPackedNode value;

		int rightExtent = key.getRightExtent();
		
		if(rightExtent  == currentLevel) {
			value = currentNodes.add(key);
			if(value == null) {
				countNonPackedNodes++;
				newNodeCreated = true;
				value = key;
			}
		} else {
			int index = indexFor(rightExtent);
			value = forwardNodes[index].add(key);
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
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		
		CuckooHashSet<NonPackedNode> currentNodes = this.currentNodes;
		
		if(currentLevel != inputSize - 1) {
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
			if(u.add(descriptor) == null) {
				 r.add(descriptor);
				 size++;
				 all++;
			} else {
				return false;
			}
		}
		
		else {
			int index = indexFor(descriptor.getInputIndex());
			if(forwardDescriptors[index].add(descriptor) == null) {
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
			value = currentGssNodes.add(key);
			if(value == null) {
				countGSSNodes++;
				value = key;
			}
		} else {
			int index = indexFor(inputIndex);
			value = forwardGssNodes[index].add(key);
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
			boolean added = currendEdges.add(edge) == null;
			if(added) {
				countGSSEdges++;
				source.addGSSEdge(edge);
			}
			return !added;
		} 
		else {
			int index = indexFor(source.getInputIndex());
			boolean added = forwardEdges[index].add(edge) == null;
			if(added) {
				countGSSEdges++;
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
					currentPackedNodes.add(packedNode);
					currentPackedNodes.add(firstPackedNode);
				} else {
					int index = indexFor(parent.getRightExtent());
					forwardPackedNodes[index].add(packedNode);
					forwardPackedNodes[index].add(firstPackedNode);
				}
				log.trace("Packed node created : %s", firstPackedNode);
				log.trace("Packed node created : %s", packedNode);
				countPackedNodes += 2;
			}
		}
		else {
			PackedNode key = new PackedNode(slot, pivot, parent);
			if(parent.getRightExtent() == currentLevel) {
				if(currentPackedNodes.add(key) == null) {
					parent.addPackedNode(key, leftChild, rightChild);
					countPackedNodes++;
				}
			} else {
				int index = indexFor(parent.getRightExtent());
				if(forwardPackedNodes[index].add(key) == null) {
					parent.addPackedNode(key, leftChild, rightChild);
					countPackedNodes++;
				}
			}
			
			log.trace("Packed node created : %s", key);
		}
	}

	@Override
	public int getPackedNodesCount() {
		return countPackedNodes;
	}

	@Override
	public int getGSSEdgesCount() {
		return countGSSEdges;
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

	@Override
	public void init(Input input) {
		terminals = new TerminalSymbolNode[chainLength + 1][2];
		
		u.clear();
		r.clear();
		
		currentNodes.clear();
		
		currentPackedNodes.clear();
		
		currentGssNodes.clear();
		
		currendEdges.clear();
		
		currentPoppedElements.clear();
		
		for(int i = 0; i < chainLength; i++) {
			forwardDescriptors[i].clear();
			forwardRs[i].clear();
			forwardNodes[i].clear();
			forwardGssNodes[i].clear();
			forwardEdges[i].clear();
			forwardPoppedElements[i].clear();
			forwardPackedNodes[i].clear();
		}
		
		currentLevel = 0;
		
		countNonPackedNodes = 0;

		countGSSNodes = 0;
		
		size = 0;
		
		all = 0;
		
		countPackedNodes = 0;
		
		countGSSEdges = 0;

	}

}
	
