package org.jgll.grammar.slotnew;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;



public class TerminalGrammarSlot implements GrammarSlot {
	
	private RegularExpression regex;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	@Override
	public GrammarSlot execute(GLLParser parser, Input input, int i) {
		return null;
	}
	
	public RegularExpression getRegularExpression() {
		return regex;
	}

}
