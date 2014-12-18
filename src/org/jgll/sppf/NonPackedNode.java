package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;


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
		
		return SPPFUtil.getInstance().equals(this, (NonPackedNode) obj);
	}

	@Override
	public int hashCode() {
		return SPPFUtil.getInstance().hash(this);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot, getLeftExtent(), getRightExtent());
	}
	
}
