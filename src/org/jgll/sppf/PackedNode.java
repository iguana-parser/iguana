package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgll.util.HashCode;

public class PackedNode extends SPPFNode {
	
	private final int pivot;
	private final int grammarPosition;
	private final NonPackedNode parent;
	
	private List<SPPFNode> children;
	
	private final int hash;
	
	public PackedNode(int grammarPosition, int pivot, NonPackedNode parent) {
		this.grammarPosition = grammarPosition;
		this.pivot = pivot;
		this.parent = parent;
		
		children = new ArrayList<>(2);
		
		hash = HashCode.hashCode(grammarPosition, pivot, parent.grammarIndex, parent.leftExtent, parent.rightExtent);
	}
			
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof PackedNode)) {
			return false;
		}
		
		PackedNode other = (PackedNode) obj;
		
		return  hash == other.hash &&
				grammarPosition == other.grammarPosition &&
		        pivot == other.pivot &&
		        parent.grammarIndex == other.parent.grammarIndex &&
		        parent.leftExtent == other.parent.leftExtent &&
		        parent.rightExtent == other.parent.rightExtent;
	}
	
	public int getPivot() {
		return pivot;
	}
	
	public int getGrammarPosition() {
		return grammarPosition;
	}
	
	public NonPackedNode getParent() {
		return parent;
	}
	
	@Override
	public void addChild(SPPFNode node) {
		children.add(node);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return String.format("(%s, %d)", getLabel(), getPivot());
	}
	
	@Override
	public String getLabel() {
		return grammarPosition + "";
	}
	
	@Override
	public String getId() {
		return parent.getId() + "," + grammarPosition + "," + pivot;
	}

	@Override
	public void replaceByChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(index);
		children.addAll(index, node.getChildren());
	}

	@Override
	public int sizeChildren() {
		return children.size();
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
	public void removeChild(SPPFNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setChildren(List<SPPFNode> children) {
		this.children = children;
	}

	@Override
	public SPPFNode childAt(int index) {
		return children.get(index);
	}

	@Override
	public void removeChildren(List<SPPFNode> node) {
		throw new UnsupportedOperationException();
	}
	
}