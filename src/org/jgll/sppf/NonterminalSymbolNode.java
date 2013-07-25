package org.jgll.sppf;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
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
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NonterminalSymbolNode)) {
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public String getLabel() {
		// TODO: fix it later: the names are not good. Use a better way to separate name
		// and indices.
		assert slot instanceof HeadGrammarSlot;
		return ((HeadGrammarSlot) slot).getNonterminal().getName();
	}

}
