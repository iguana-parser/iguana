package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonPackedNode {
	
	private PackedNode[] packedNodes;
	
	private int firstPivot;
		
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	public void addPackedNode(GrammarSlot s, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		if(packedNodes.length == 0) {
			if(leftChild != DummyNode.getInstance()) {
				children.add(leftChild);
			}
			children.add(rightChild);
			firstPivot = pivot;
		} 
		else if (packedNodes.length == 1) {
			// Packed node does not exist
			if(pivot != firstPivot) {
				packedNodes = new PackedNode[rightExtent - leftExtent];
				children.clear();
				children.add(new PackedNode(slot, firstPivot, this));
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				children.add(newPackedNode);
				packedNodes[pivot] = newPackedNode;
			}
		}
		else {
			if(packedNodes[pivot - leftExtent] == null) {
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				packedNodes[pivot] = newPackedNode;
			}
		}		
	}
	
	public void addFirstPackedNode(GrammarSlot slot, int pivot) {
		firstPivot = pivot;
	}
	
	@Override
	public boolean isAmbiguous() {
		return packedNodes.length > 1;
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

}