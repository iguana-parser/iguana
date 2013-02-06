package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgll.grammar.GrammarSlot;
import org.jgll.util.HashCode;

/**
 * A NonPackedNode corresponds to nonterminal symbol nodes or
 * intermediate symbols nodes in the GLL paper. These nodes 
 * have the common property of being related to a grammar slot
 * in the body of production rules.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class NonPackedNode extends SPPFNode {
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	protected List<SPPFNode> children;
	
	private int packedNodeCount;
	
	private int firstPackedNodePivot = -1;
	
	private GrammarSlot firstPackedNodeGrammarSlot = null;
	
	private final int hash;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		children = new ArrayList<>(2);
		hash = HashCode.hashCode(slot, leftExtent, rightExtent);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof NonPackedNode)) {
			return false;
		}
		
		NonPackedNode other = (NonPackedNode) obj;

		return  hash == other.hash &&
				slot.equals(other.slot) &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public int getLeftExtent() {
		return leftExtent;
	}

	public int getRightExtent() {
		return rightExtent;
	}
	
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), leftExtent, rightExtent);
	}
	
	@Override
	public String getId() {
		return slot.getId() + "," + leftExtent + "," + rightExtent;
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}
	
	@Override
	public List<SPPFNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public void addPackedNode(PackedNode newPackedNode, SPPFNode leftChild, SPPFNode rightChild) {
		// Don't store the first packed node as the node may not be ambiguous
		if(packedNodeCount == 0) {
			firstPackedNodeGrammarSlot = newPackedNode.getGrammarSlot();
			firstPackedNodePivot = newPackedNode.getPivot();
			if(!leftChild.equals(DummyNode.getInstance())) {
				children.add(leftChild);
			}
			children.add(rightChild);
			packedNodeCount++;
			return;
		}
		
		// When the second packed node is about to be added, create the first packed
		// node and add it. Then create the next packed node.
		if (packedNodeCount == 1) {
			
			PackedNode firstPackedNode = getFirstPackedNode();

			for(SPPFNode child : children) {
				firstPackedNode.addChild(child);
			}
			
			children.clear();
			children.add(firstPackedNode);
			
			// the second packed node
			addChildren(newPackedNode, leftChild, rightChild);
			children.add(newPackedNode);
		} 
		
		else {
			addChildren(newPackedNode, leftChild, rightChild);
			children.add(newPackedNode);
		}
		
		packedNodeCount++;
	}
	
	public boolean hasPackedNode(GrammarSlot grammarSlot, int pivot) {
		return firstPackedNodeGrammarSlot.equals(grammarSlot) &&
			   firstPackedNodePivot == pivot;
	}
	
	private static void addChildren(PackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
		if (!leftChild.equals(DummyNode.getInstance())) {
			parent.addChild(leftChild);
		}
		parent.addChild(rightChild);
	}
	
	public boolean isAmbiguous() {
		return packedNodeCount > 1;
	}
	
	public void addChild(SPPFNode node) {
		children.add(node);
	}
	
	public int countPackedNode() {
		return packedNodeCount;
	}
	
	public PackedNode getFirstPackedNode() {
		return new PackedNode(firstPackedNodeGrammarSlot, firstPackedNodePivot, this);
	}

	public GrammarSlot getFirstPackedNodeGrammarSlot() {
		return firstPackedNodeGrammarSlot;
	}

}
