package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

public class OriginalIntermediateNode extends IntermediateNode {
	
	private Set<PackedNode> packedNodes;

	public OriginalIntermediateNode(int id, int leftExtent, int rightExtent) {
		super(id, leftExtent, rightExtent);
	}
	
	@Override
	public OriginalIntermediateNode init() {
		super.init();
		packedNodes = new HashSet<>();
		return this;
	}
	
	
	@Override
	public boolean addPackedNode(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {
		PackedNode newPackedNode = attachChildren(packedNode, leftChild, rightChild);
		if (!packedNodes.contains(newPackedNode)) {
			children.add(newPackedNode);
			packedNodes.add(newPackedNode);
			return true;
		}
		return false;
	}

}
