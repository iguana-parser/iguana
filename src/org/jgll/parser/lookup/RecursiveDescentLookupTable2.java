package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.MultiHashSet;
import org.jgll.util.logging.LoggerWrapper;

public class RecursiveDescentLookupTable2 extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(RecursiveDescentLookupTable2.class);
	
	private HashTableFactory factory; 
	
	private int tableSize = (int) Math.pow(2, 10);
	
	private Deque<Descriptor> descriptorsStack;
	
	private TerminalSymbolNode[] terminals;
	
	private MultiHashSet<Descriptor>[] descriptorsSet;
	
	private MultiHashSet<NonPackedNode>[] nonPackedNodes;

	private MultiHashSet<GSSNode>[] gssNodes;
	
	private int nonPackedNodesCount;
	
	public RecursiveDescentLookupTable2(Grammar grammar) {
		super(grammar);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Input input) {
		
		terminals = new TerminalSymbolNode[2 * input.size()];

		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new MultiHashSet[input.size()];
		nonPackedNodes = new MultiHashSet[input.size()];
		gssNodes = new MultiHashSet[input.size()];
		
		nonPackedNodesCount = 0;
		
		factory = HashTableFactory.getFactory();
	}
	
	@Override
	public GSSNode getGSSNode(GrammarSlot grammarSlot, int inputIndex) {
		
		MultiHashSet<GSSNode> set = gssNodes[inputIndex];
		if(set == null) {
			set = factory.newHashSet(tableSize, GSSNode.levelBasedExternalHasher);
			gssNodes[inputIndex] = set;
			GSSNode key = new GSSNode(grammarSlot, inputIndex);
			set.add(key);
			return key;
		}
		
		GSSNode key = new GSSNode(grammarSlot, inputIndex);
		
		GSSNode oldValue = set.add(key);
		
		if(oldValue == null) {
			return key;
		}
		
		return oldValue;
	}
	
	@Override
	public int getGSSNodesCount() {
		int count = 0;
		for(int i = 0; i < gssNodes.length; i++) {
			if(gssNodes[i] != null) {
				count += gssNodes[i].size();
			}
		}
		return count;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		List<GSSNode> nodes = new ArrayList<>();
		for(int i = 0; i < gssNodes.length; i++) {
			if(gssNodes[i] != null) {
				for(GSSNode node : gssNodes[i]) {
					nodes.add(node);
				}
			}
		}
		return nodes;
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
		
		MultiHashSet<Descriptor> set = descriptorsSet[descriptor.getInputIndex()];
		if(set == null) {
			set = factory.newHashSet(tableSize, Descriptor.levelBasedExternalHasher);
			descriptorsSet[descriptor.getInputIndex()] = set;
			set.add(descriptor);
			descriptorsStack.add(descriptor);
			return true;
		}
		
		Descriptor add = set.add(descriptor);

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
		int index = key.getRightExtent();
		MultiHashSet<NonPackedNode> set = nonPackedNodes[index];
		
		if(set == null) {
			set = factory.newHashSet(tableSize, NonPackedNode.levelBasedExternalHasher);
			nonPackedNodes[index] = set;
			set.add(key);
			return key;
		}
		
		NonPackedNode oldValue = nonPackedNodes[index].add(key);
		if(oldValue == null) {
			oldValue = key;
		}
		
		return oldValue;
	}
	
	@Override
	public NonPackedNode hasNonPackedNode(NonPackedNode key) {
		int index = key.getRightExtent();
		if(nonPackedNodes[index] == null) {
			return null;
		}
		return nonPackedNodes[index].get(key);
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		if(nonPackedNodes[inputSize - 1] == null) {
			return null;
		}
		return (NonterminalSymbolNode) nonPackedNodes[inputSize - 1].get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int getNonPackedNodesCount() {
		return nonPackedNodesCount;
	}

	@Override
	public int getDescriptorsCount() {
		int count = 0;
		for(int i = 0; i < descriptorsSet.length; i++) {
			if(descriptorsSet[i] != null) {
				count += descriptorsSet[i].size();
			}
		}
		return count;
	}

	@Override
	public void addPackedNode(NonPackedNode parent, GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		PackedNode packedNode = new PackedNode(slot, pivot, parent);
		parent.addPackedNode(packedNode, leftChild, rightChild);
	}

	@Override
	public int getPackedNodesCount() {
		return 0;
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
