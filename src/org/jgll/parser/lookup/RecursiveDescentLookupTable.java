package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.logging.LoggerWrapper;

public class RecursiveDescentLookupTable extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(RecursiveDescentLookupTable.class);
	
	private Deque<Descriptor> descriptorsStack;
	
	private CuckooHashSet<Descriptor> descriptorsSet;
	
	private TerminalSymbolNode[] terminals;
	
	private CuckooHashSet<NonPackedNode> nonPackedNodes;
	
	private final CuckooHashSet<GSSNode> gssNodes;
	
	private final CuckooHashSet<PackedNode> packedNodes;
	
	private int nonPackedNodesCount;
	
	public RecursiveDescentLookupTable(Grammar grammar) {
		super(grammar);
		
		int tableSize = (int) Math.pow(2, 22);
		
		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new CuckooHashSet<>(tableSize, Descriptor.externalHasher);
		nonPackedNodes = new CuckooHashSet<>(tableSize, NonPackedNode.externalHasher);
		gssNodes = new CuckooHashSet<>(tableSize, GSSNode.externalHasher);
		packedNodes = new CuckooHashSet<>(tableSize, PackedNode.externalHasher);
	}
	
	@Override
	public void init(Input input) {
		terminals = new TerminalSymbolNode[2 * input.size()];
		descriptorsStack.clear();
		descriptorsSet.clear();
		
		nonPackedNodes.clear();
		gssNodes.clear();
		packedNodes.clear();
		
		nonPackedNodesCount = 0;
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
		Descriptor add = descriptorsSet.add(descriptor);
		if(add == null) {
			descriptorsStack.add(descriptor);
			return true;
		}
		
		return false;
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
			log.trace("Terminal node created: %s", terminal);
			terminals[index] = terminal;
			nonPackedNodesCount++;
		}
		
		return terminal;
	}
	
	@Override
	public NonPackedNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		return getNonPackedNode(key);
	}
	
	@Override
	public NonPackedNode hasNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		return hasNonPackedNode(key);
	}
	
	@Override
	public NonPackedNode getNonPackedNode(NonPackedNode key) {
		NonPackedNode value = nonPackedNodes.add(key);
		if(value == null) {
			value = key;
		}
		
		return value;
	}
	
	@Override
	public NonPackedNode hasNonPackedNode(NonPackedNode key) {
		return nonPackedNodes.get(key);
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
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
	public boolean getGSSEdge(GSSNode source, SPPFNode node, GSSNode destination) {
		return source.createEdge(destination, node);
	}

	@Override
	public int getGSSEdgesCount() {
		return 0;
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		gssNode.addToPoppedElements(sppfNode);
	}

	@Override
	public Iterable<NonPackedNode> getSPPFNodesOfPoppedElements(GSSNode gssNode) {
		return gssNode.getPoppedElements();
	}

	@Override
	public Iterable<GSSNode> getChildren(GSSNode node) {
		return node.getChildren();
	}

	@Override
	public Iterable<SPPFNode> getSPPFNodeOnEdgeFrom(GSSNode source, GSSNode dest) {
		return source.getNodesForChild(dest);
	}

}
