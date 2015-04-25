package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;


public abstract class NonterminalOrIntermediateNode extends NonPackedNode {

	protected List<PackedNode> children;
	private PackedNodeSet set;
	
	public NonterminalOrIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set) {
		super(slot, leftExtent, rightExtent);
		this.set = set;
		children = new ArrayList<>();
	}

	public void addChild(PackedNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (set.addPackedNode(packedNode.getGrammarSlot(), packedNode.getPivot())) {
			children.add(packedNode);
			packedNode.addChild(leftChild);
			packedNode.addChild(rightChild);
			return true;
		}
		return false;
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode child) {
		if (set.addPackedNode(packedNode.getGrammarSlot(), packedNode.getPivot())) {
			children.add(packedNode);
			packedNode.addChild(child);
			return true;
		}
		return false;	
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
