package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;

public class RegularListNode extends NonterminalSymbolNode {

	private boolean partial;
	
	private Regul
	
	public RegularListNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	public boolean isPartial() {
		return partial;
	}
	
	public void setPartial(boolean partial) {
		this.partial = partial;
	}
}
