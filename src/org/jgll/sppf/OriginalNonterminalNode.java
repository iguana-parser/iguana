package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.NonterminalGrammarSlot;


public class OriginalNonterminalNode extends NonterminalNode {

	private Set<PackedNode> packedNodes;
	
	public OriginalNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		super(head, leftExtent, rightExtent);
	}	
	
	@Override
	public OriginalNonterminalNode init() {
		super.init();
		packedNodes = new HashSet<>();
		return this;
	}
	
	@Override
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		PackedNode newPackedNode = attachChildren(packedNode, leftChild, rightChild);
		if (!packedNodes.contains(newPackedNode)) {
			children.add(newPackedNode);
			packedNodes.add(newPackedNode);
			return true;
		}
		return false;
	}
	
}
