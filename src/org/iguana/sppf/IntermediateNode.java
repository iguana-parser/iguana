package org.iguana.sppf;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.traversal.SPPFVisitor;

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