package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;

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

}