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

	public LL1NonterminalGrammarSlot(int id, int nodeId, String label, BodyGrammarSlot previous, LL1HeadGrammarSlot nonterminal) {
		super(id, nodeId, label, previous, nonterminal);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(!nonterminal.test(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			parser.recordParseError(this);
			return null;
		}
		
//		if(executePreConditions(parser, lexer)) {
//			return null;
//		}

		nonterminal.parse(parser, lexer);
		
		SPPFNode node = null;
		
		if(node == null) {
			return null;
		}
		
		if(next instanceof LastGrammarSlot) {
			parser.getNonterminalNode((LastGrammarSlot) next, parser.getCurrentSPPFNode(), node);
			
//			if(executePopActions(parser, lexer)) {
//				return null;
//			}
			parser.pop();
			
		} else {
			parser.getIntermediateNode(next, parser.getCurrentSPPFNode(), node);
			return next;
		}
		
		return null;
	}
	
}