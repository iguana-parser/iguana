package org.jgll.sppf;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
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
	
	private int firstPivot;
	
	private LastGrammarSlot firstPackedNodeSlot;
	
	private final int numberOfAlternatives;
	
	public NonterminalSymbolNode(HeadGrammarSlot head, int leftExtent, int rightExtent) {
		super(head, leftExtent, rightExtent);
		this.numberOfAlternatives = head.getCountAlternates();
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	public void addPackedNode(LastGrammarSlot s, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild  != null;
		assert rightChild != null;
		
		if(packedNodes.length == 0) {
			if(leftChild != DummyNode.getInstance()) {
				children.add(leftChild);
			}
			children.add(rightChild);
			firstPivot = pivot;
			firstPackedNodeSlot = s;
		} 
		else if (packedNodes.length == 1) {
			// Packed node does not exist
			if(pivot != firstPivot) {
				packedNodes = new PackedNode[numberOfAlternatives][rightExtent - leftExtent];
				children.clear();
				children.add(new PackedNode(slot, firstPivot, this));
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				children.add(newPackedNode);
				packedNodes[s.getAlternateIndex()][pivot - leftExtent] = newPackedNode;
			}
		}
		else {
			if(packedNodes[pivot - leftExtent] == null) {
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				packedNodes[s.getAlternateIndex()][pivot - leftExtent] = newPackedNode;
			}
		}		
	}
	
	public void addFirstPackedNode(LastGrammarSlot slot, int pivot) {
		firstPackedNodeSlot = slot;
		firstPivot = pivot;
	}
	
	@Override
	public String getLabel() {
		// TODO: fix it later: the names are not good. Use a better way to separate name
		// and indices.
		assert slot instanceof HeadGrammarSlot;
		return ((HeadGrammarSlot) slot).getNonterminal().getName();
	}
	
	@Override
	public LastGrammarSlot getFirstPackedNodeGrammarSlot() {
		return firstPackedNodeSlot;
	}

	@Override
	public boolean isAmbiguous() {
		return packedNodes.length > 1;
	}

}
