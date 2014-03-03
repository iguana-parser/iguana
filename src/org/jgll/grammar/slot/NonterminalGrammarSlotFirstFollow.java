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
		
		if(!nonterminal.check(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			parser.recordParseError(this);
			return null;
		}
		
		if(executePreConditions(parser, lexer)) {
			return null;
		}
		
		if(parser.isLLOptimizationEnabled() && nonterminal.isLL1SubGrammar()) {
			SPPFNode node = nonterminal.parseLL1(parser, lexer);
			
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
				
		parser.createGSSNode(next, nonterminal);
		return nonterminal;
	}

	
}