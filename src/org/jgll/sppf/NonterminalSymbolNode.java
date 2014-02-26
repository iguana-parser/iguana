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
		
		if(countPackedNodes == 0) {
			if(leftChild != DummyNode.getInstance()) {
				children.add(leftChild);
			}
			children.add(rightChild);
			firstPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
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
				LastGrammarSlot grammarSlot = (LastGrammarSlot) firstPackedNode.getGrammarSlot();
				packedNodes[grammarSlot.getAlternateIndex()][firstPackedNode.getPivot() - leftExtent] = firstPackedNode;
				
				// Add the second packed node
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				packedNodes[s.getAlternateIndex()][pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}
		else {
			if(packedNodes[s.getAlternateIndex()][pivot - leftExtent] == null) {
				PackedNode newPackedNode = attachChildren(new PackedNode(s, pivot, this), leftChild, rightChild);
				packedNodes[s.getAlternateIndex()][pivot - leftExtent] = newPackedNode;
				children.add(newPackedNode);
				countPackedNodes++;
			}
		}		
	}
	
	public void addFirstPackedNode(LastGrammarSlot slot, int pivot) {
		firstPackedNode = new PackedNode(slot, pivot, this);
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
		return (LastGrammarSlot) firstPackedNode.getGrammarSlot();
	}

}
