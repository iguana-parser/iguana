package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.HashCode;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
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
	public List<SPPFNode> getChildren() {
		return Collections.unmodifiableList(children);
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
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
}