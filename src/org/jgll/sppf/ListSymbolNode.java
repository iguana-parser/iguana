package org.jgll.sppf;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * Represents an intermediary node resulting from EBNF to BNF conversion of 
 * X* and X+, where X is a grammar symbol. 
 * 
 * @author Ali Afroozeh
 *
 */
public class ListSymbolNode extends NonterminalSymbolNode {

	public ListSymbolNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public <T> void accept(SPPFVisitor<T> visitAction, T t) {
		visitAction.visit(this, t);
	}

}
