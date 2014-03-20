package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class OnlyOneTokenSlotWithPreCondition extends OnlyOneTokenSlot {
	
	
	private final ConditionsTest conditions;

	public OnlyOneTokenSlotWithPreCondition(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression,
			int tokenID, ConditionsTest conditions) {
		super(id, nodeId, label, previous, regularExpression, tokenID);
		this.conditions = conditions;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
				
		GrammarSlot result = super.parse(parser, lexer);
		
		if(conditions.execute(parser, lexer)) {
			return null;
		}

		return result;
	}

}
