package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;


public class EpsilonSlotWithPreCondition extends EpsilonGrammarSlot {

	private static final long serialVersionUID = 1L;

	private ConditionsTest preConditions;

	public EpsilonSlotWithPreCondition(int id, String label, HeadGrammarSlot head, ConditionsTest preConditions) {
		super(id, label, head);
		this.preConditions = preConditions;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(preConditions.execute(parser, lexer)) {
			return null;
		}
		
		return super.parse(parser, lexer);
	}
}
