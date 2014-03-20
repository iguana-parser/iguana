package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;


public class NonterminalGrammarSlotWithPreCondition extends NonterminalGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private ConditionsTest preConditions;

	public NonterminalGrammarSlotWithPreCondition(int id, int nodeId, String label, BodyGrammarSlot previous, 
			HeadGrammarSlot nonterminal, ConditionsTest preConditions) {
		super(id, nodeId, label, previous, nonterminal);
		this.preConditions = preConditions;
	}

	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if (preConditions.execute(parser, lexer)) {
			return null;
		}
		
		return super.parse(parser, lexer);
	}

}
