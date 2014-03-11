package org.jgll.sppf;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonPackedNode {
	
	private PackedNode[] packedNodes;
	
	private PackedNode firstPackedNode;
		
	public IntermediateNode(int id, int leftExtent, int rightExtent) {
		super(id, leftExtent, rightExtent);
	}
	
	public void addPackedNode(int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		if(countPackedNodes == 0) {
			if(leftChild != DummyNode.getInstance()) {
				children.add(leftChild);
			}
			children.add(rightChild);
			firstPackedNode = attachChildren(new PackedNode(id, pivot, this), leftChild, rightChild);
			countPackedNodes++;
		} 
		else if (countPackedNodes == 1) {
			// Packed node does not exist
			if(pivot != firstPackedNode.getPivot()) {
				
				// Initialize the packed nodes array for duplicate elimination
				packedNodes = new PackedNode[rightExtent - leftExtent + 1];
				
				// Add the first packed node
				children.clear();
				children.add(firstPackedNode);
				packedNodes[firstPackedNode.getPivot() - leftExtent] = firstPackedNode;
				
				// Add the second packed node
				PackedNode newPackedNode = attachChildren(new PackedNode(id, pivot, this), leftChild, rightChild);
				packedNodes[pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}
		else {
			if(packedNodes[pivot - leftExtent] == null) {
				PackedNode newPackedNode = attachChildren(new PackedNode(id, pivot, this), leftChild, rightChild);
				packedNodes[pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}		
	}
	
	public void addFirstPackedNode(int packedNodeId, int pivot) {
		firstPackedNode = new PackedNode(packedNodeId, pivot, this);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

}