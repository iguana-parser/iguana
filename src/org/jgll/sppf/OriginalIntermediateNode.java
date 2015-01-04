package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;

public class OriginalIntermediateNode extends IntermediateNode {
	
	private Set<PackedNode> packedNodes;

	public OriginalIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
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
