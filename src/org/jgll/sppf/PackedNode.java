package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.VisitAction;
import org.jgll.util.HashCode;

public class PackedNode extends SPPFNode implements Modifiable {
	
	private final int pivot;
	private final GrammarSlot slot;
	private final SPPFNode parent;
	
	private List<SPPFNode> children;
	
	private final int hash;
	
	public PackedNode(GrammarSlot slot, int pivot, SPPFNode parent) {
		this.slot = slot;
		this.pivot = pivot;
		this.parent = parent;
		
		children = new ArrayList<>(2);
		
		hash = HashCode.hashCode(slot, pivot, parent);
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
				slot.equals(other.slot) &&
		        pivot == other.pivot &&
		        parent.equals(other.parent);
	}
	
	public int getPivot() {
		return pivot;
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public SPPFNode getParent() {
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
		return slot.toString();
	}
	
	@Override
	public String getId() {
		return parent.getId() + "," + slot.getId() + "," + pivot;
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

	@Override
	public int getLeftExtent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRightExtent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(VisitAction visitAction) {
		visitAction.visit(this);
	}
	
}