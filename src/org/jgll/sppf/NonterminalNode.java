package org.jgll.sppf;

import java.util.ArrayList;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonPackedNode {
	
	
	public NonterminalNode(int nonterminalId, int numberOfAlternatives, int leftExtent, int rightExtent) {
		super(nonterminalId, leftExtent, rightExtent);
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
