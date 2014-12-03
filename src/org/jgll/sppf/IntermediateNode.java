package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
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
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), leftExtent, rightExtent);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if (!(obj instanceof IntermediateNode)) {
			return false;
		}
		
		IntermediateNode other = (IntermediateNode) obj;

		return  slot == other.getGrammarSlot() &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	@Override
	public IntermediateNode init() {
		children = new ArrayList<>(2);
		return this;
	}
}