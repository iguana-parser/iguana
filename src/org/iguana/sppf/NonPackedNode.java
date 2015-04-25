package org.iguana.sppf;

import java.util.List;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.HashFunctions;


/**
 * 
 * A NonPackedNode is the abstract super class for nonterminal 
 * and intermediate symbol nodes.
 * 
 * 
 * @author Ali Afroozeh
 * 
 */

public abstract class NonPackedNode implements SPPFNode {
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
	}

	public int getLeftExtent() {
		return leftExtent;
	}
	
	public int getRightExtent() {
		return rightExtent;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if (!(obj instanceof NonPackedNode)) 
			return false;
		
		NonPackedNode other = (NonPackedNode) obj;
		
		return slot == other.slot && 
			   leftExtent == other.leftExtent && 
			   rightExtent == other.rightExtent;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), leftExtent, rightExtent);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot, leftExtent, rightExtent);
	}
	
	public abstract List<PackedNode> getChildren();
	
}
