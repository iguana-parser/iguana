package org.jgll.grammar.slot;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.SPPFNode;


/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class LL1NonterminalGrammarSlot extends NonterminalGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	public LL1NonterminalGrammarSlot(int position, BodyGrammarSlot previous, LL1HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(position, previous, nonterminal, head);
	}
	
	public LL1NonterminalGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		LL1NonterminalGrammarSlot slot = new LL1NonterminalGrammarSlot(this.position, previous, (LL1HeadGrammarSlot) nonterminal, head);
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

		nonterminal.parse(parser, lexer);
		
		SPPFNode node = null;
		
		if(node == null) {
			return null;
		}
		
		if(next instanceof LastGrammarSlot) {
			parser.getNonterminalNode((LastGrammarSlot) next, node);
			
			if(executePopActions(parser, lexer)) {
				return null;
			}
			parser.pop();
			
		} else {
			parser.getIntermediateNode(next, node);
			return next;
		}
		
		return null;
	}
	
}