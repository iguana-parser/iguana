package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalSymbolNode extends NonPackedNode {
	
	private boolean keywordNode;

	public NonterminalSymbolNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	public boolean isKeywordNode() {
		return keywordNode;
	}
	
	public void setKeywordNode(boolean keywordNode) {
		this.keywordNode = keywordNode;
	}
	
	@Override
	public String getLabel() {
		// TODO: fix it later: the names are not good. Use a better way to separate name
		// and indices.
		assert slot instanceof HeadGrammarSlot;
		return ((HeadGrammarSlot) slot).getNonterminal().getName();
	}

}
