package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
	private final BodyGrammarSlot slot;

	private final int pivot;

	private final SPPFNode parent;
	
	private List<SPPFNode> children;
	
	public PackedNode(BodyGrammarSlot slot, int pivot, NonPackedNode parent) {
		
		assert slot != null;
		assert pivot >= 0;
		assert parent != null;
		
		this.slot = slot;
		this.pivot = pivot;
		this.parent = parent;
		this.children = new ArrayList<>(2);
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
		
		return  slot == other.slot && pivot == other.pivot;
	}
	
	public int getPivot() {
		return pivot;
	}

	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public SPPFNode getParent() {
		return parent;
	}
	
	public void addChild(SPPFNode node) {
		children.add(node);
	}

	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
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
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), pivot);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d)", slot, getPivot());
	}
	
	@Override
	public int getLeftExtent() {
		return parent.getLeftExtent();
	}

	@Override
	public int getRightExtent() {
		return parent.getRightExtent();
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}

	@Override
	public int childrenCount() {
		return children.size();
	}

	@Override
	public List<SPPFNode> getChildren() {
		return children;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

}