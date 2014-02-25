package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;

/**
 * A NonPackedNode is the abstract super class for nonterminal and intermediate
 * symbol nodes.
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
	
	private final int hash;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		assert slot != null;
		
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		this.children = new ArrayList<>();
		
		this.hash = HashFunctions.defaulFunction().hash(slot.getId(), leftExtent, rightExtent); 
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

		// Only nodes with the same concrete class can be equal.
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		
		NonPackedNode other = (NonPackedNode) obj;

		return  slot == other.slot &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	@Override
	public int getLeftExtent() {
		return leftExtent;
	}

	@Override
	public int getRightExtent() {
		return rightExtent;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), leftExtent, rightExtent);
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}

	/**
	 * Attaches the given left and right children to the given packed node.
	 *  
	 */
	protected PackedNode attachChildren(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {
		
		if (leftChild != DummyNode.getInstance()) {
			packedNode.addChild(leftChild);
		}
		
		packedNode.addChild(rightChild);
		
		return packedNode;
	}
	
	
	public void addChild(SPPFNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
		if(node instanceof PackedNode) {
		}
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	/**
	 * Replaces the given node with its children.
	 * If this does node have the given node as child,
	 * nothing will happen.  
	 */
	public void replaceWithChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(node);
		if(index >= 0) {
			for(SPPFNode child : node.getChildren()) {
				children.add(index++, child);				
			}
		}
	}
	
	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}
	
	@Override
	public SPPFNode getFirstChild() {
		return children.get(0);
	}
	
	@Override
	public SPPFNode getLastChild() {
		if(children.size() == 0) {
			return null;
		}
		return children.get(children.size() - 1);
	}
	
	@Override
	public Iterable<SPPFNode> getChildren() {
		return children;
	}
	
	@Override
	public int childrenCount() {
		return children.size();
	}
	
}
