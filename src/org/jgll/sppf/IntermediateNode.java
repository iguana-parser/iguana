package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNode extends NonterminalOrIntermediateNode {
	
	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public IntermediateNode init() {
		children = new ArrayList<>(2);
		return this;
	}

	@Override
	public BodyGrammarSlot getGrammarSlot() {
		return (BodyGrammarSlot) slot;
	}
}