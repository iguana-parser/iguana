package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * This implementation is optimized for input files with an average number
 * of line of codes (less than 3000).
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class DefaultLookupTableImpl extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(DefaultLookupTableImpl.class);
	
	private HashTableFactory factory; 
	
	private int tableSize = (int) Math.pow(2, 10);
	
	private Deque<Descriptor> descriptorsStack;
	
	private IguanaSet<Descriptor>[] descriptorsSet;
	
	private IguanaSet<NonPackedNode>[] nonPackedNodes;
	
	private IguanaSet<PackedNode>[] packedNodes;

	/**
	 *  Elements indexed by GSS nodes (Nonterminal index and input index)
	 */
	private GSSTuple[][] gssTuples;
	
	private TokenSymbolNode[][] tokenSymbolNodes;
	
	private int nonPackedNodesCount;
	
	public DefaultLookupTableImpl(Grammar grammar) {
		super(grammar);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Input input) {
		
		long start = System.nanoTime();
		
		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new IguanaSet[input.size()];
		nonPackedNodes = new IguanaSet[input.size()];
		packedNodes = new IguanaSet[input.size()];
		
		gssTuples = new GSSTuple[grammar.getNonterminals().size()][input.size()];
		
		tokenSymbolNodes = new TokenSymbolNode[grammar.getCountTokens()][input.size()];
		
		long end = System.nanoTime();
		log.info("Lookup table initialization: %d ms", (end - start) / 1000_000);
		
		nonPackedNodesCount = 0;
		
		factory = HashTableFactory.getFactory();
	}
	
	@Override
	public GSSNode getGSSNode(HeadGrammarSlot head, int inputIndex) {
		
		GSSTuple gssTuple = gssTuples[head.getId()][inputIndex];
		
		if(gssTuple == null) {
			gssTuple = new GSSTuple();
			gssTuples[head.getId()][inputIndex] = gssTuple;
		}
		
		GSSNode gssNode = gssTuple.getGssNode();
		if(gssNode == null) {
			gssNode = new GSSNode(head, inputIndex);
		}
		 
		return gssNode;
	}
	
	@Override
	public int getGSSNodesCount() {
		int count = 0;
//		for(int i = 0; i < gssNodes.length; i++) {
//			if(gssNodes[i] != null) {
//				count ++;
//			}
//		}
		return count;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		List<GSSNode> nodes = new ArrayList<>();
//		for(int i = 0; i < gssNodes.length; i++) {
//			if(gssNodes[i] != null) {
//				for(GSSNode node : gssNodes[i]) {
//					nodes.add(node);
//				}
//			}
//		}
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
	
	@SuppressWarnings("unused")
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
		int count = 0;
		for (int i = 0; i < packedNodes.length; i++) {
			if(packedNodes[i] == null) {
				continue;
			}
			count += packedNodes[i].size();
		}
		return count;
	}
	
	@Override
	public boolean getGSSEdge(GSSNode source, GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
		
		GSSTuple gssTuple = gssTuples[source.getGrammarSlot().getId()][source.getInputIndex()];
		
		IguanaSet<GSSEdge> set = gssTuple.getGssEdges();
		
		GSSEdge edge = new GSSEdge(returnSlot, node, destination);
		
		return set.add(edge) == null;
	}

	@Override
	public int getGSSEdgesCount() {
		return 0;
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		GSSTuple gssTuple = gssTuples[gssNode.getGrammarSlot().getId()][gssNode.getInputIndex()];
		gssTuple.getNonPackedNodes().add(sppfNode);
	}

	@Override
	public Iterable<NonPackedNode> getPoppedElementsOf(GSSNode gssNode) {
		GSSTuple gssTuple = gssTuples[gssNode.getGrammarSlot().getId()][gssNode.getInputIndex()];
		return gssTuple.getNonPackedNodes();
	}

	@Override
	public Iterable<GSSNode> getChildren(GSSNode node) {
		return node.getChildren();
	}
	
	@Override
	public Iterable<GSSEdge> getEdges(GSSNode node) {
		GSSTuple gssTuple = gssTuples[node.getGrammarSlot().getId()][node.getInputIndex()];
		return gssTuple.getGssEdges();
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
