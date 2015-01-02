package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.util.hashing.ExternalHashEquals;


public class OriginalNonterminalNode extends NonterminalNode {

	private Set<PackedNode> packedNodes;
	
	public OriginalNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		super(head, leftExtent, rightExtent, hashEquals);
	}	
	
	@Override
	public OriginalNonterminalNode init() {
		super.init();
		packedNodes = new HashSet<>();
		return this;
	}
	
	@Override
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (!packedNodes.contains(packedNode)) {
			children.add(packedNode);
			packedNode.addChild(leftChild);
			packedNode.addChild(rightChild);
			packedNodes.add(packedNode);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode child) {
		if (!packedNodes.contains(packedNode)) {
			children.add(packedNode);
			packedNode.addChild(child);
			packedNodes.add(packedNode);
			return true;
		}
		return false;	
	}
	
}
