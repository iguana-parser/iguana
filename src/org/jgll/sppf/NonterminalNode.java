package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonPackedNode {
	
	
	public NonterminalNode(int nonterminalId, int leftExtent, int rightExtent) {
		super(nonterminalId, leftExtent, rightExtent);
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
		
		if (!(obj instanceof NonterminalNode)) {
			return false;
		}
		
		NonterminalNode other = (NonterminalNode) obj;

		return  id == other.id &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public NonterminalNode init() {
		children = new ArrayList<>(2);
		return this;
	}
	
	@Override
	public int getFirstPackedNodeGrammarSlot() {
		return children.get(0).getId();
	}

}
