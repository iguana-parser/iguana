package org.jgll.lookup;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.CuckooHashSet;

/**
 * 
 * This class provides a skeletal implementation of {@link LookupTable}.
 * Based on how descriptors are removed from the set of
 * descriptors, e.g., using a stack or a queue, the implementation of
 * LookupTables may vary. This class only provides an implementation for 
 * lookup functionalities which are the same for different strategies.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class AbstractLookupTable implements LookupTable {
	
	protected final Grammar grammar;
	
	private final CuckooHashSet<GSSNode> gssNodes;
	
	protected final int inputSize;
	
	protected final int slotsSize;
	
	protected int gssEdgesCount;
	
	public AbstractLookupTable(Grammar grammar, int inputSize) {
		this.inputSize = inputSize;
		this.grammar = grammar;
		this.slotsSize = grammar.getGrammarSlots().size();
		this.gssNodes = new CuckooHashSet<>();
	}

	@Override
	public boolean hasGSSEdge(GSSNode source, SPPFNode label, GSSNode destination) {
		 boolean added = source.hasGSSEdge(label, destination);
		 if(!added) {
			 gssEdgesCount++;
		 }
		 return added;
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot grammarSlot, int inputIndex) {	
		GSSNode gssNode = new GSSNode(grammarSlot, inputIndex);
		if(gssNodes.contains(gssNode)) {
			return gssNodes.get(gssNode);
		}
		else {
			gssNodes.add(gssNode);
			return gssNode;
		}
//		return gssNodes.addAndGet(new GSSNode(grammarSlot, inputIndex));
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode) {
		gssNode.addToPopElements(sppfNode);
	}
	
	@Override
	public Iterable<SPPFNode> getSPPFNodesOfPoppedElements(GSSNode gssNode) {
		return gssNode.getPoppedElements();
	}

	@Override
	public int getGSSNodesCount() {
		return gssNodes.size();
	}
	
	@Override
	public int getGSSEdgesCount() {
		return gssEdgesCount;
	}
	
	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return gssNodes;
	}
	
	protected NonPackedNode createNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key;
		if(slot instanceof HeadGrammarSlot) {
			HeadGrammarSlot head = (HeadGrammarSlot) slot;
			if(head.getNonterminal().isEbnfList()) {
				key = new ListSymbolNode(slot, leftExtent, rightExtent);
			} else {
				key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
			}
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		return key;
	}
}
