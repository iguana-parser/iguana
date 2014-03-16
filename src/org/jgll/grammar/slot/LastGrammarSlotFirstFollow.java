package org.jgll.grammar.slot;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;


/**
 * This subclass checks for the next character in the follow set before the pop action.
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlotFirstFollow extends LastGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	public LastGrammarSlotFirstFollow(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(id, label, previous, head);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			parser.pop();			
		}
		return null;
	}

	

}
