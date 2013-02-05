package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

public class NonterminalSymbolNode extends NonPackedNode {
	
	public NonterminalSymbolNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public void addChild(SPPFNode node) {
		super.addChild(node);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof NonterminalSymbolNode)) {
			return false;
		}
		
		return super.equals(obj);
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
}
