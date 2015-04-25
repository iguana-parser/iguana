package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonterminalOrIntermediateNode {
	
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set) {
		super(slot, leftExtent, rightExtent, set);
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return (GrammarSlot) slot;
	}
}