package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;


public class OriginalNonterminalNode extends NonterminalNode {

	private Set<PackedNode> packedNodes;
	
	public OriginalNonterminalNode(int nonterminalId, int leftExtent, int rightExtent) {
		super(nonterminalId, leftExtent, rightExtent);
	}	
	
	@Override
	public OriginalNonterminalNode init() {
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
