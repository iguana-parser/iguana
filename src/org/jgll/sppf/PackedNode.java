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
public class PackedNode implements SPPFNode {
	
	private final BodyGrammarSlot slot;

	private final int pivot;

	private final NonPackedNode parent;
	
	private List<NonPackedNode> children;
	
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
		if(this == obj) 
			return true;

		if (!(obj instanceof PackedNode)) 
			return false;
		
		PackedNode other = (PackedNode) obj;
		
		return  slot == other.slot && pivot == other.pivot;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.hashCode(), pivot);
	}
	
	public int getPivot() {
		return pivot;
	}

	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public NonPackedNode getParent() {
		return parent;
	}
	
	public void addChild(NonPackedNode node) {
		children.add(node);
	}

	public void removeChild(SPPFNode node) {
		children.remove(node);
	}

	@Override
	public String toString() {
		return String.format("(%s, %d)", slot, getPivot());
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
	public List<NonPackedNode> getChildren() {
		return children;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}
}