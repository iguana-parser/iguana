package org.jgll.sppf;

import java.util.List;

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
	
	protected final int id;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	protected List<SPPFNode> children;
	
	public NonPackedNode(int id, int leftExtent, int rightExtent) {
		
		assert id >= 0;
		assert leftExtent >= 0;
		assert rightExtent >= 0;
		
		this.id = id;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
	}

	public abstract NonPackedNode init();
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(id, leftExtent, rightExtent);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) {
			return false;
		}
		
		if(this == obj) {
			return true;
		}

		// Only nodes with the same concrete class can be equal.
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		
		NonPackedNode other = (NonPackedNode) obj;

		return  id == other.id &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	

	@Override
	public int getId() {
		return id;
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
		return String.format("(%d, %d, %d)", id, leftExtent, rightExtent);
	}
	
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
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
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
	
	public int getFirstPackedNodeGrammarSlot() {
		return id;
	}
	
	public int getCountPackedNodes() {
		return children.size();
	}
	
}
