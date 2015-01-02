package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.ExternalHashEquals;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonterminalOrIntermediateNode {
	
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		super(slot, leftExtent, rightExtent, hashEquals);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public IntermediateNode init() {
		children = new ArrayList<>();
		return this;
	}

	@Override
	public GrammarSlot getGrammarSlot() {
		return (GrammarSlot) slot;
	}
}