package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

public class RegularListNode extends NonPackedNode {

	private boolean partial;
	
	public RegularListNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	public boolean isPartial() {
		return partial;
	}
	
	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		
	}

}
