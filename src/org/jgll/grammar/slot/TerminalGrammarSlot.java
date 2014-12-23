package org.jgll.grammar.slot;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.NonPackedNode;
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
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
	}
	
	public RegularExpression getRegularExpression() {
		return regex;
	}

}
