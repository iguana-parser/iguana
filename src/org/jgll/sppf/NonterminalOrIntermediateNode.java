package org.jgll.sppf;

import java.util.List;


public abstract class NonterminalOrIntermediateNode extends NonPackedNode {

	protected List<SPPFNode> children;
	
	public abstract NonterminalOrIntermediateNode init();
	
	public NonterminalOrIntermediateNode(Object slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
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
		
		if (leftChild != DummyNode.getInstance())
			packedNode.addChild(leftChild);
		
		if (rightChild != DummyNode.getInstance())
			packedNode.addChild(rightChild);
		
		return packedNode;
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
	
	public boolean isAmbiguous() {
		return children.size() > 1;
	}
	
	public int getCountPackedNodes() {
		return children.size();
	}
}
