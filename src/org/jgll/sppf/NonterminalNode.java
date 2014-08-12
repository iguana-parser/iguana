package org.jgll.sppf;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonPackedNode {
	
	
	public NonterminalNode(int nonterminalId, int numberOfAlternatives, int leftExtent, int rightExtent) {
		super(nonterminalId, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	/**
	 * Packed node id is effectively the alternate index from which this packed node is
	 * going to be created. 
	 */
	public boolean addPackedNode(int packedNodeId, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		PackedNode newPackedNode = attachChildren(new PackedNode(packedNodeId, pivot, this), leftChild, rightChild);
		children.add(newPackedNode);
		
		return true;
	}
	
	@Override
	public int getFirstPackedNodeGrammarSlot() {
		return children.get(0).getId();
	}

}
