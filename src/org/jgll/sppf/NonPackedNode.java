package org.jgll.sppf;

import java.util.List;

/**
 * 
 * A NonPackedNode is the abstract super class for nonterminal 
 * and intermediate symbol nodes.
 * 
 * 
 * @author Ali Afroozeh
 * 
 */

public abstract class NonPackedNode implements SPPFNode {
	
	protected final Object slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	protected List<SPPFNode> children;
	
	public NonPackedNode(Object slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
	}

	public abstract NonPackedNode init();
	
	public boolean addPackedNode(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {
		PackedNode newPackedNode = attachChildren(packedNode, leftChild, rightChild);
		children.add(newPackedNode);
		return true;
	}
	
	/**
	 * Attaches the given left and right children to the given packed node.
	 *  
	 */
	protected PackedNode attachChildren(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {
		
		if (leftChild != DummyNode.getInstance()) {
			packedNode.addChild(leftChild);
		}
		
		if (rightChild != DummyNode.getInstance()) {
			packedNode.addChild(rightChild);
		}
		
		return packedNode;
	}

	public void addChild(SPPFNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
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
		return index < children.size() ? children.get(index) : null;
	}
	
	@Override
	public List<SPPFNode> getChildren() {
		return children;
	}
	
	@Override
	public int childrenCount() {
		return children.size();
	}
	
	@Override
	public boolean isAmbiguous() {
		return children.size() > 1;
	}
	
	public int getCountPackedNodes() {
		return children.size();
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
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if (!(obj instanceof SPPFNode)) 
			return false;
		
		return SPPFUtil.getInstance().equals(this, (SPPFNode) obj);
	}

	@Override
	public int hashCode() {
		return SPPFUtil.getInstance().hash(this);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", getGrammarSlot(), getLeftExtent(), getRightExtent());
	}
	
	@FunctionalInterface
	public static interface Equals {
		public boolean equals(SPPFNode node, SPPFNode other);
	}
	
	@FunctionalInterface
	public static interface Hash {
	  public int hash(SPPFNode node);
	}
}
