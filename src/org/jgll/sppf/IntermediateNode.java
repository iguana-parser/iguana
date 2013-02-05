package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.VisitAction;

public class IntermediateNode extends NonPackedNode {
		
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof IntermediateNode)) {
			return false;
		}
		
		return super.equals(obj);
	}

	@Override
	public void accept(VisitAction visitAction) {
		visitAction.visit(this);
	}

}