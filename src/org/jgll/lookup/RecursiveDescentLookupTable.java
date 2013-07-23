package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.Descriptor.DescriptorDecomposer;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.hashing.CuckooHashMap;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.Decomposer;
import org.jgll.util.logging.LoggerWrapper;

public class RecursiveDescentLookupTable extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(RecursiveDescentLookupTable.class);
	
	private static final Decomposer<Descriptor> descriptorDecomposer = new DescriptorDecomposer();
	private static final Decomposer<GSSNode> gssNodeDecomposer = new GSSNode.GSSNodeDecomposer();
	private static final Decomposer<GSSEdge> gssEdgeDecomposer = new GSSEdge.GSSEdgeDecomposer();
	private static final Decomposer<NonPackedNode> nonPackedNodesDecomposer = new NonPackedNode.NonPackedNodeDecomposer();
	private static final Decomposer<PackedNode> packedNodeDecomposer = new PackedNode.PackedNodeDecomposer();

	private Deque<Descriptor> descriptorsStack;
	
	private CuckooHashSet<Descriptor> descriptorsSet;
	
	private TerminalSymbolNode[] terminals;
	
	private CuckooHashMap<NonPackedNode, NonPackedNode> nonPackedNodes;
	
	private final CuckooHashSet<GSSNode> gssNodes;
	
	private final CuckooHashSet<PackedNode> packedNodes;
	
	private final CuckooHashSet<GSSEdge> gssEdges;
	
	private int nonPackedNodesCount;
	
	private CuckooHashMap<GSSNode, Set<SPPFNode>> poppedElements;
	
	public RecursiveDescentLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new CuckooHashSet<>(descriptorDecomposer);
		terminals = new TerminalSymbolNode[2 * inputSize];
		nonPackedNodes = new CuckooHashMap<>(inputSize, nonPackedNodesDecomposer);
		gssNodes = new CuckooHashSet<>(gssNodeDecomposer);
		packedNodes = new CuckooHashSet<>(packedNodeDecomposer);
		gssEdges = new CuckooHashSet<>(gssEdgeDecomposer);
		poppedElements = new CuckooHashMap<>(gssNodeDecomposer);
	}
	
	@Override
	public GSSNode getGSSNode(GrammarSlot grammarSlot, int inputIndex) {	
		GSSNode key = new GSSNode(grammarSlot, inputIndex);
		GSSNode value = gssNodes.add(key);
		if(value == null) {
			return key;
		}
		return value;
	}
	
	@Override
	public int getGSSNodesCount() {
		return gssNodes.size();
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return gssNodes;
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
	public boolean addDescriptor(Descriptor descriptor) {
		if(descriptorsSet.contains(descriptor)) {
			return false;
		}

		descriptorsStack.push(descriptor);
		descriptorsSet.add(descriptor);
		return true;
	}

	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		int index = 2 * leftExtent;
		if(terminalIndex != TerminalSymbolNode.EPSILON) {
			index = index + 1;
		}

		TerminalSymbolNode terminal = terminals[index];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			log.trace("Terminal node created: {}", terminal);
			terminals[index] = terminal;
			nonPackedNodesCount++;
		}
		
		return terminal;
	}

	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		NonPackedNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		
		NonPackedNode value = nonPackedNodes.get(key);
		if(value == null) {
			value = key;
			nonPackedNodes.put(key, value);
			nonPackedNodesCount++;
		}
		
		return value;
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol) {
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
				packedNodes.add(packedNode);
				packedNodes.add(firstPackedNode);
			}
		}
		else {
			PackedNode key = new PackedNode(slot, pivot, parent);
			if(packedNodes.add(key) == null) {
				parent.addPackedNode(key, leftChild, rightChild);
			}
		}
	}

	@Override
	public int getPackedNodesCount() {
		return packedNodes.size();
	}

	@Override
	public boolean hasGSSEdge(GSSNode source, SPPFNode label, GSSNode destination) {
		GSSEdge edge = new GSSEdge(source, label, destination);
		boolean added = gssEdges.add(edge) == null;
		if(added) {
			source.addGSSEdge(edge);
		}
		return !added;
	}

	@Override
	public int getGSSEdgesCount() {
		return gssEdges.size();
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode) {
		Set<SPPFNode> set = poppedElements.get(gssNode);
		if(set == null) {
			set = new HashSet<>();
			poppedElements.put(gssNode, set);
		}
		set.add(sppfNode);
	}

	@Override
	public Iterable<SPPFNode> getSPPFNodesOfPoppedElements(GSSNode gssNode) {
		Set<SPPFNode> set = poppedElements.get(gssNode);
		if(set == null) {
			 set = Collections.emptySet();
		}
		return set;
	}

}
