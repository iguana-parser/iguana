package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.hashing.ExternalHashEquals;

public class OriginalIntermediateNode extends IntermediateNode {
	
	private Set<PackedNode> packedNodes;

	public OriginalIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		super(slot, leftExtent, rightExtent, hashEquals);
	}
	
	@Override
	public OriginalIntermediateNode init() {
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
