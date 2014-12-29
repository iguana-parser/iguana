package org.jgll.sppf;

import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;


public abstract class NonterminalOrIntermediateNode extends NonPackedNode {

	protected List<PackedNode> children;
	
	public abstract NonterminalOrIntermediateNode init();
	
	public NonterminalOrIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}

	public void addChild(PackedNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode child) {
		packedNode.addChild(child);
		children.add(packedNode);
		return true;
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		packedNode.addChild(leftChild);
		packedNode.addChild(rightChild);
		children.add(packedNode);
		return true;
	}
	
	@Override
	public PackedNode getChildAt(int index) {
		return index < children.size() ? children.get(index) : null;
	}
	
	@Override
	public List<PackedNode> getChildren() {
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
