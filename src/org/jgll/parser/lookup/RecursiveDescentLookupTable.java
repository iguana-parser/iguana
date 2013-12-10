package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;
import org.jgll.util.logging.LoggerWrapper;

public class RecursiveDescentLookupTable extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(RecursiveDescentLookupTable.class);
	
	private HashTableFactory factory; 
	
	private int tableSize = (int) Math.pow(2, 10);
	
	private Deque<Descriptor> descriptorsStack;
	
	private TerminalSymbolNode[] terminals;
	
	private IguanaSet<Descriptor>[] descriptorsSet;
	
	private IguanaSet<NonPackedNode>[] nonPackedNodes;
	
	private IguanaSet<PackedNode>[] packedNodes;

	private GSSNode[][] gssNodes;
	
	private List<NonPackedNode>[][] poppedElements;
		
	private IguanaSet<GSSEdge>[][] gssEdges;
	
	private TokenSymbolNode[][] tokenSymbolNodes;
	
	private int nonPackedNodesCount;
	
	public RecursiveDescentLookupTable(Grammar grammar) {
		super(grammar);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Input input) {
		
		terminals = new TerminalSymbolNode[2 * input.size()];

		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new IguanaSet[input.size()];
		nonPackedNodes = new IguanaSet[input.size()];
		packedNodes = new IguanaSet[input.size()];
		
		gssNodes = new GSSNode[grammar.getNonterminals().size()][input.size()];
		poppedElements = new List[grammar.getNonterminals().size()][input.size()];
		gssEdges = new IguanaSet[grammar.getNonterminals().size()][input.size()];
		
		int tokensSize = grammar.getRegularExpressions().size() + grammar.getKeywords().size();
		tokenSymbolNodes = new TokenSymbolNode[tokensSize][input.size()];
		
		nonPackedNodesCount = 0;
		
		factory = HashTableFactory.getFactory();
	}
	
	@Override
	public GSSNode getGSSNode(HeadGrammarSlot head, int inputIndex) {
		
		GSSNode gssNode = gssNodes[head.getId()][inputIndex];
		
		if(gssNode == null) {
			gssNode = new GSSNode(head, inputIndex);
			gssNodes[head.getId()][inputIndex] = gssNode;
		}
		
		return gssNode;
	}
	
	@Override
	public int getGSSNodesCount() {
		int count = 0;
		for(int i = 0; i < gssNodes.length; i++) {
			if(gssNodes[i] != null) {
				count ++;
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
		
		IguanaSet<Descriptor> set = descriptorsSet[descriptor.getInputIndex()];
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
		IguanaSet<NonPackedNode> set = nonPackedNodes[index];
		
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
	
	private int getTotalCollisions() {
		int total = 0;
		
		for(int i = 0; i < descriptorsSet.length; i++) {
			if(descriptorsSet[i] != null) {
				total += descriptorsSet[i].getCollisionCount();
			}
		}
		
		for(int i = 0; i < nonPackedNodes.length; i++) {
			if(nonPackedNodes[i] != null) {
				total += nonPackedNodes[i].getCollisionCount();
			}
		}
		
		return total;
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		
		System.out.println(getTotalCollisions());
		
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
		
		int index = parent.getRightExtent();
		IguanaSet<PackedNode> set = packedNodes[index];
		
		PackedNode packedNode = new PackedNode(slot, pivot, parent);
		
		if(set == null) {
			set = factory.newHashSet(tableSize, PackedNode.levelBasedExternalHasher);
			packedNodes[index] = set;
			parent.addPackedNode(packedNode, leftChild, rightChild);
			set.add(packedNode);
			return;
		}
		
		PackedNode add = set.add(packedNode);
		if(add == null) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		}
	}

	@Override
	public int getPackedNodesCount() {
		return 0;
	}

	@Override
	public boolean getGSSEdge(GSSNode source, GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
		
		GSSEdge edge = new GSSEdge(returnSlot, node, destination);
		
		IguanaSet<GSSEdge> set = gssEdges[source.getGrammarSlot().getId()][source.getInputIndex()];
		
		if(set == null) {
			set = factory.newHashSet(GSSEdge.externalHasher);
			gssEdges[source.getGrammarSlot().getId()][source.getInputIndex()] = set;
			set.add(edge);
			return true;
		}
		
		return set.add(edge) == null;
	}

	@Override
	public int getGSSEdgesCount() {
		return 0;
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		List<NonPackedNode> list = poppedElements[gssNode.getGrammarSlot().getId()][gssNode.getInputIndex()];
		if(list == null) {
			list = new ArrayList<>();
			poppedElements[gssNode.getGrammarSlot().getId()][gssNode.getInputIndex()] = list;
		}
		
		list.add(sppfNode);
	}

	@Override
	public Iterable<NonPackedNode> getPoppedElementsOf(GSSNode gssNode) {
		List<NonPackedNode> list = poppedElements[gssNode.getGrammarSlot().getId()][gssNode.getInputIndex()];
		if(list == null) {
			return Collections.emptyList();
		}
		
		return list;
	}

	@Override
	public Iterable<GSSNode> getChildren(GSSNode node) {
		return node.getChildren();
	}
	
	@Override
	public Iterable<GSSEdge> getEdges(GSSNode node) {
		IguanaSet<GSSEdge> set = gssEdges[node.getGrammarSlot().getId()][node.getInputIndex()];
		if(set == null) {
			return Collections.emptySet();
		}
		return set;
	}
	
	@Override
	public TokenSymbolNode getTokenSymbolNode(int tokenID, int inputIndex, int length) {
		TokenSymbolNode node = tokenSymbolNodes[tokenID][inputIndex];
		if(node == null) {
			node = new TokenSymbolNode(tokenID, inputIndex, length);
			tokenSymbolNodes[tokenID][inputIndex] = node;
		}
		return node;
	}

}
