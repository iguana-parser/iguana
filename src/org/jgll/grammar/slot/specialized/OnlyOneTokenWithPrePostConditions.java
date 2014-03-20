package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class OnlyOneTokenWithPrePostConditions extends OnlyOneTokenSlot {
	
	private static final long serialVersionUID = 1L;


	private ConditionsTest preConditions;
	private ConditionsTest postConditions;

	public OnlyOneTokenWithPrePostConditions(int id, int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression,
			int tokenID, ConditionsTest preConditions, ConditionsTest postConditions) {
		super(id, nodeId, label, previous, regularExpression, tokenID);
		this.preConditions = preConditions;
		this.postConditions = postConditions;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(preConditions.execute(parser, lexer)) {
			return null;
		}
		
		GrammarSlot result = super.parse(parser, lexer);
		
		if(postConditions.execute(parser, lexer)) {
			return null;
		}
		
		return result;
	}

}
