package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalSymbolNode extends NonPackedNode {

	public NonterminalSymbolNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public <T> void accept(SPPFVisitor<T> visitAction, T t) {
		visitAction.visit(this, t);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NonterminalSymbolNode)) {
			return false;
		}
		return super.equals(obj);
	}

}
