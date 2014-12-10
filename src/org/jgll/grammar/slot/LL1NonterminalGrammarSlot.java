package org.jgll.grammar.slot;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.SPPFNode;


/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class LL1NonterminalGrammarSlot extends NonterminalGrammarSlot {
	
	public LL1NonterminalGrammarSlot(String label, BodyGrammarSlot previous, 
									 LL1HeadGrammarSlot nonterminal, 
									 ConditionTest preConditions, 
									 ConditionTest popConditions,
									 NodeCreator nodeCreator) {
		super(label, previous, nonterminal, preConditions, popConditions, nodeCreator);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		
		if(!nonterminal.test(lexer.charAt(parser.getCurrentInputIndex()))) {
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
		
//		if(next instanceof LastGrammarSlot) {
//			parser.getNonterminalNode((LastGrammarSlot) next, parser.getCurrentSPPFNode(), node);
			
//			if(executePopActions(parser, lexer)) {
//				return null;
//			}
//			parser.pop();
//			
//		} else {
//			parser.getIntermediateNode(next, parser.getCurrentSPPFNode(), node);
//			return next;
//		}
		
		return null;
	}
	
}