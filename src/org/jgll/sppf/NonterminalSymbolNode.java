package org.jgll.sppf;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalSymbolNode extends NonPackedNode {
	
	/**
	 * Packed nodes under nonterminal nodes are of the form (slot, index) where
	 * slot can be one of the alternates of the nonterminal. The input index varies
	 * from the left-extent to the right-extent of the nonterminal node.
	 * 
	 * The first index of this array is the slot id and the second one is the input index.
	 */
	private PackedNode[][] packedNodes;
	
	private PackedNode firstPackedNode;
	
	private final int numberOfAlternatives;
	
	public NonterminalSymbolNode(int nonterminalId, int numberOfAlternatives, int leftExtent, int rightExtent) {
		super(nonterminalId, leftExtent, rightExtent);
		this.numberOfAlternatives = numberOfAlternatives;
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	/**
	 * Packed node id is effectively the alternate index from which this packed node is
	 * going to be created. 
	 */
	public void addPackedNode(int packedNodeId, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		if(countPackedNodes == 0) {
			if(leftChild != DummyNode.getInstance()) {
				children.add(leftChild);
			}
			children.add(rightChild);
			firstPackedNode = attachChildren(new PackedNode(packedNodeId, pivot, this), leftChild, rightChild);
			countPackedNodes++;
		} 
		else if (countPackedNodes == 1) {
			// if packed node does not exist
			if(pivot != firstPackedNode.getPivot()) {
				// Initialize the packed nodes array for duplicate elimination
				packedNodes = new PackedNode[numberOfAlternatives][rightExtent - leftExtent + 1];
				
				// Add the first packed node
				children.clear();
				children.add(firstPackedNode);
				int firstPackedNodeId = firstPackedNode.getId();
				packedNodes[firstPackedNodeId][firstPackedNode.getPivot() - leftExtent] = firstPackedNode;
				
				// Add the second packed node
				PackedNode newPackedNode = attachChildren(new PackedNode(packedNodeId, pivot, this), leftChild, rightChild);
				packedNodes[packedNodeId][pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}
		else {
			if(packedNodes[packedNodeId][pivot - leftExtent] == null) {
				PackedNode newPackedNode = attachChildren(new PackedNode(packedNodeId, pivot, this), leftChild, rightChild);
				packedNodes[packedNodeId][pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}		
	}
	
	public void addFirstPackedNode(int id, int pivot) {
		firstPackedNode = new PackedNode(id, pivot, this);
	}
	
	@Override
	public int getFirstPackedNodeGrammarSlot() {
		return firstPackedNode.getId();
	}

}
