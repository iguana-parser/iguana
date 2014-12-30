package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.SPPFToJavaCode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonterminalOrIntermediateNode {
	
	public NonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
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
	public NonterminalGrammarSlot getGrammarSlot() {
		return (NonterminalGrammarSlot) slot;
	}

	public String toJavaCode() {
		return SPPFToJavaCode.toJavaCode(this);
	}
	
}
