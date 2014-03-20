package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSEdge;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlotWithPostConditions extends LastGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	public LastGrammarSlotWithPostConditions(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(id, label, previous, head);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		for(GSSEdge edge : parser.getCurrentGSSNode().getGSSEdges()) {
			if(edge.getReturnSlot().getPreConditions().execute(parser, lexer)) {
				continue;
			}
			parser.pop();
		}
		
		return null;
	}
	
}
