package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgll.parser.GLLParser;

public abstract class NonPackedNodeWithChildren extends NonPackedNode {

	protected List<SPPFNode> children;
	
	private int packedNodeCount;
	
	private int firstPackedNodePivot = -1;
	
	private int firstPackedNodeGrammarPisiton = -1;
	
	public NonPackedNodeWithChildren(int grammarIndex, int leftExtent, int rightExtent) {
		super(grammarIndex, leftExtent, rightExtent);
		children = new ArrayList<>(2);
	}
	
	@Override
	public void addChild(SPPFNode node) {
		children.add(node);
	}
	
	@Override
	public int sizeChildren() {
		return children.size();
	}
	
	@Override
	public void replaceByChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(index);
		children.addAll(index, node.getChildren());
	}

	@Override
	public List<SPPFNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public SPPFNode firstChild() {
		return children.get(0);
	}
	
	@Override
	public SPPFNode childAt(int index) {
		return children.get(index);
	}
	
	@Override
	public void removeChildren(List<SPPFNode> node) {
		children.removeAll(node);
	}
	
	@Override
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	@Override
	public void setChildren(List<SPPFNode> children) {
		this.children = children;
	}
	
	@Override
	public void addPackedNode(PackedNode newPackedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		// Don't store the first packed node as the node may not be ambiguous
		if(packedNodeCount == 0) {
			firstPackedNodeGrammarPisiton = newPackedNode.getGrammarPosition();
			firstPackedNodePivot = newPackedNode.getPivot();
			if(!leftChild.equals(GLLParser.DUMMY)) {
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
	
	public boolean hasPackedNode(int grammarPosition, int pivot) {
		return firstPackedNodeGrammarPisiton == grammarPosition &&
			   firstPackedNodePivot == pivot;
	}
	
	private static void addChildren(PackedNode parent, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (!leftChild.equals(GLLParser.DUMMY)) {
			parent.addChild(leftChild);
		}
		parent.addChild(rightChild);
	}
	
	@Override
	public boolean isAmbiguous() {
		return packedNodeCount > 1;
	}
	
	
	public int countPackedNode() {
		return packedNodeCount;
	}
	
	public PackedNode getFirstPackedNode() {
		return new PackedNode(firstPackedNodeGrammarPisiton, firstPackedNodePivot, this);
	}
	
}
