package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonPackedNode {
		
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IntermediateNode)) {
			return false;
		}
		return super.equals(obj);
	}

}