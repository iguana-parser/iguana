package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Rule;
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

	public NonterminalGrammarSlotFirstFollow(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(rule, position, slotId, label, previous, nonterminal, head);
	}
	
	public NonterminalGrammarSlotFirstFollow copy(BodyGrammarSlot previous, String label, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		NonterminalGrammarSlotFirstFollow slot = new NonterminalGrammarSlotFirstFollow(rule, position, slotId, label, previous, nonterminal, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		System.out.println(nonterminal.getNonterminal().getName());
		
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