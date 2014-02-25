package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalSymbolNode extends NonPackedNode {
	
	// input index, slot id
	private PackedNode[][] packedNodes;
	
	private int firstPivot;
	
	public NonterminalSymbolNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	public void addPackedNode(GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
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
	
	@Override
	public String getLabel() {
		// TODO: fix it later: the names are not good. Use a better way to separate name
		// and indices.
		assert slot instanceof HeadGrammarSlot;
		return ((HeadGrammarSlot) slot).getNonterminal().getName();
	}
	
	public LastGrammarSlot getFirstPackedNodeGrammarSlot() {
		return (LastGrammarSlot) firstPackedNode.getGrammarSlot();
	}

	@Override
	public boolean isAmbiguous() {
		return packedNodes.length > 1;
	}

}
