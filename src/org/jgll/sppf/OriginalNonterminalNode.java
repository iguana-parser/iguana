package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.NonterminalGrammarSlot;

public class OriginalNonterminalNode extends NonterminalNode {

	private Set<PackedNode> packedNodes;
	
	public OriginalNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		super(head, leftExtent, rightExtent);
		packedNodes = new HashSet<>();
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
