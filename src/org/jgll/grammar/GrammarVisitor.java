package org.jgll.grammar;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Alternate;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarVisitor {
	
	public static void visit(Grammar grammar, GrammarVisitAction action) {
		for(HeadGrammarSlot head : grammar.getNonterminals()) {
			visit(head, action);
		}
	}
	
	public static void visit(Iterable<HeadGrammarSlot> heads, GrammarVisitAction action) {
		for(HeadGrammarSlot head : heads) {
			visit(head, action);
		}		
	}

	public static void visit(HeadGrammarSlot root, GrammarVisitAction action) {
		action.visit(root);
		
		for(Alternate alternate : root.getAlternates()) {
			
			BodyGrammarSlot currentSlot = alternate.getFirstSlot();
			
			while(!(currentSlot instanceof LastGrammarSlot)) {
				
				if(currentSlot instanceof NonterminalGrammarSlot) {
					action.visit((NonterminalGrammarSlot) currentSlot);						
				}
				
				else if (currentSlot instanceof TerminalGrammarSlot) {
					action.visit((TerminalGrammarSlot) currentSlot);
				}
				
				else if (currentSlot instanceof KeywordGrammarSlot) {
					action.visit((KeywordGrammarSlot) currentSlot);
				}
				
				currentSlot = currentSlot.next();
			}
			
			action.visit((LastGrammarSlot)currentSlot);
		}
	}

}
