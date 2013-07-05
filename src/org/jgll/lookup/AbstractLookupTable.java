package org.jgll.lookup;

import java.util.ArrayList;
import java.util.Collection;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

/**
 * 
 * This class provides a skeletal implementation of {@link LookupTable}.
 * Based on how descriptors are removed from the set of
 * descriptors, e.g., using a stack or a queue, the implementation of
 * LookupTables may vary. This class only provides an implementation for 
 * lookup functionalities which are the same for different remove
 * strategies.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class AbstractLookupTable implements LookupTable {
	
	protected final Grammar grammar;
	
	private GSSNode[][] gssNodes;
	
	protected final int inputSize;
	
	protected final int slotsSize;
	
	protected int gssEdgesCount;
	
	public AbstractLookupTable(Grammar grammar, int inputSize) {
		this.inputSize = inputSize;
		this.grammar = grammar;
		gssNodes = new GSSNode[grammar.getGrammarSlots().size()][];
		slotsSize = grammar.getGrammarSlots().size();
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
	public GSSNode getGSSNode(GrammarSlot grammarSlot, int inputIndex, int alternateInputIndex) {
		int slotId = grammarSlot.getId();
		if(gssNodes[slotId] == null) {
			gssNodes[slotId] = new GSSNode[inputSize];
		}
		if(gssNodes[slotId][inputIndex] == null) {
			gssNodes[slotId][inputIndex] = new GSSNode(grammarSlot, inputIndex, alternateInputIndex);
		} 
		
		return gssNodes[slotId][inputIndex];
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
		return getGSSNodes().size();
	}
	
	@Override
	public int getGSSEdgesCount() {
		return gssEdgesCount;
	}
	
	@Override
	public Collection<GSSNode> getGSSNodes() {
		Collection<GSSNode> list = new ArrayList<>();
		for(int i = 0; i < gssNodes.length; i++) {
			if(gssNodes[i] != null) {
			for(int j = 0; j < gssNodes[i].length; j++) {
				if(gssNodes[i][j] != null) {
					list.add(gssNodes[i][j]);
				}
			}
			}
		}
		return list;
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
