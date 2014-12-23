package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;

public class OriginalIntermediateNode extends IntermediateNode {
	
	private Set<PackedNode> packedNodes;

	public OriginalIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public OriginalIntermediateNode init() {
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
