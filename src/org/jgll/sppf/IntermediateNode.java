package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonPackedNode {
	
	public IntermediateNode(int id, int leftExtent, int rightExtent) {
		super(id, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(id, leftExtent, rightExtent);
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

		return  id == other.id &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	@Override
	public IntermediateNode init() {
		children = new ArrayList<>(2);
		return this;
	}

}