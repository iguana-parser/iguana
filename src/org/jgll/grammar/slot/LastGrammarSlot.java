package org.jgll.grammar.slot;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	protected HeadGrammarSlot head;
	
	public LastGrammarSlot(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head, 
						   ConditionTest popConditions, NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, null, null, popConditions, null, nodeCreatorFromPop);
		this.head = head;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			return parser.pop();			
		}
		return null;
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}

	@Override
	public Symbol getSymbol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void code(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConstructorCode() {
		// TODO Auto-generated method stub
		return null;
	}

}
