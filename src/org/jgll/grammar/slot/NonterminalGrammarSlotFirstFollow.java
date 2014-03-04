package org.jgll.grammar.slot;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;


/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlotFirstFollow extends NonterminalGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	public NonterminalGrammarSlotFirstFollow(int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(position, previous, nonterminal, head);
	}
	
	public NonterminalGrammarSlotFirstFollow copy(BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		NonterminalGrammarSlotFirstFollow slot = new NonterminalGrammarSlotFirstFollow(this.position, previous, nonterminal, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(!nonterminal.test(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			parser.recordParseError(this);
			return null;
		}
		
		if(executePreConditions(parser, lexer)) {
			return null;
		}
				
		parser.createGSSNode(next, nonterminal);
		return nonterminal;
	}

	
}