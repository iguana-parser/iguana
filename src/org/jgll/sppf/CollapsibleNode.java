package org.jgll.sppf;

import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.util.hashing.ExternalHashEquals;




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

	public CollapsibleNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		super(head, leftExtent, rightExtent, hashEquals);
	}
	
	@Override
	public NonterminalNode init() {
		super.init();
		return this;
	}

}
