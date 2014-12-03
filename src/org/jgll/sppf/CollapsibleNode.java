package org.jgll.sppf;

import org.jgll.grammar.slot.HeadGrammarSlot;



/**
 * 
 * A collapsible node is a nonterminal symbol node that will be replaced
 * by its children after the parse tree is built. This node can be used,
 * for example, to represent nonterminals as part of the left-factorization
 * process. 
 * 
 * @author Ali Afroozeh
 *
 */
public class CollapsibleNode extends NonterminalNode {

	public CollapsibleNode(HeadGrammarSlot head, int leftExtent, int rightExtent) {
		super(head, leftExtent, rightExtent);
	}
	
	@Override
	public NonterminalNode init() {
		super.init();
		return this;
	}

}
