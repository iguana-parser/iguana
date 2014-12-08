package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonPackedNode {
	
	public NonterminalNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.hashCode(), leftExtent, rightExtent);
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

		return  slot == other.slot &&
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
	public HeadGrammarSlot getGrammarSlot() {
		return (HeadGrammarSlot) slot;
	}

	
}
