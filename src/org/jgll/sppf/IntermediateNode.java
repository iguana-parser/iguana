package org.jgll.sppf;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonPackedNode {
	
	public IntermediateNode(int id, int leftExtent, int rightExtent) {
		super(id, leftExtent, rightExtent);
	}
	
	public boolean addPackedNode(int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		PackedNode newPackedNode = attachChildren(new PackedNode(id, pivot, this), leftChild, rightChild);
		children.add(newPackedNode);
		return true;		
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

}